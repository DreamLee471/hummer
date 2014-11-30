/*
 * Copyright 2014 Dream.Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hummer.client.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.api.client.Client;
import org.hummer.api.exception.HummerRemotingException;
import org.hummer.api.server.ResponseFuture;
import org.hummer.client.ClientFactory;
import org.hummer.client.conf.ClientMetaData;
import org.hummer.config.AddressService;
import org.hummer.config.LoadBanlanceService;
import org.hummer.remoting.ResponseStatus;
import org.hummer.serialize.HessianSerializer;
import org.hummer.service.ServiceLocator;
import org.hummer.util.HummerUtils;

public class ProxyFactory {
	
	/**
	 * 目标地址服务
	 */
	private static AddressService addressService=ServiceLocator.loadService(AddressService.class, "default");
	private static LoadBanlanceService lbService=ServiceLocator.loadService(LoadBanlanceService.class, "robin");
	
	public static Object getProxy(ClientMetaData metadata) throws ClassNotFoundException{
		Class<?> c=Class.forName(metadata.getService());
		if(c.isInterface()){
			return createJDKDynamicProxy(metadata);
		}
		throw new HummerRemotingException(500, metadata.getService()+" is not interface");
	}
	
	private static Object createJDKDynamicProxy(ClientMetaData metadata) throws IllegalArgumentException, ClassNotFoundException {
		return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), new Class[]{Class.forName(metadata.getService())}, new ClientMethodInvocationHandler(metadata));
	}

	public static class ClientMethodInvocationHandler implements InvocationHandler{

		private ClientMetaData metadata;
		private static AtomicLong id=new AtomicLong();
		
		public ClientMethodInvocationHandler(ClientMetaData metadata){
			this.metadata=metadata;
			addressService.watch(metadata.getService(), metadata.getVersion());
		}
		
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			String methodDesc=HummerUtils.getMethodDescriptor(method);
			RpcRequest request=new RpcRequest();
			request.setRequestId(id.incrementAndGet());
			request.setSerializer(new HessianSerializer());
			request.setServiceName(metadata.getService());
			request.setVersion(metadata.getVersion());
			request.setMethodDecorator(methodDesc);
			request.setArgs(args);
			List<String> targetAddress=addressService.getTargetAddress(metadata.getService(), metadata.getVersion(), methodDesc);
			String target=lbService.select(targetAddress);
			if(target==null) throw new RuntimeException("can't find target service");
			ResponseFuture future=new ResponseFuture();
			ResponseFuture.RESPONSE_FUTURES.put(request.getRequestId(), future);
			String[] hostport=target.split(":");
			Client client=ClientFactory.getClent(hostport[0], Integer.parseInt(hostport[1]));
			client.sendRequest(request);
			
			RpcResponse resp=null;
			resp=future.get(metadata.getTimeout(), TimeUnit.MILLISECONDS);
			if(resp==null){
				//TODO 重试
				throw new RuntimeException("timeout");
			}
			if(ResponseStatus.OK.getCode()==resp.getResponseCode()){
				return resp.getData();
			}else if(resp.getResponseCode()==500&&resp.getException()!=null){
				throw new RuntimeException(resp.getException());
			}else{
				//TODO 重试
				throw new HummerRemotingException(resp.getResponseCode(), resp.getResponseDesc());
			}
		}
	}
}
