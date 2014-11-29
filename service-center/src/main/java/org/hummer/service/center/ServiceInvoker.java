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
package org.hummer.service.center;

import org.apache.log4j.Logger;
import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.remoting.ResponseStatus;
import org.hummer.service.center.register.ServiceRegistry;
import org.hummer.service.center.register.ServiceRegistry.ServiceMethodWraper;

public class ServiceInvoker {
	
	private final Logger logger=Logger.getLogger(ServiceInvoker.class);
	
	private static ServiceInvoker instance;
	
	public static ServiceInvoker getInstance(){
		if(instance==null){
			instance=new ServiceInvoker();
		}
		return instance;
	}
	
	private ServiceInvoker(){
		
	}
	
	public RpcResponse invoke(RpcRequest request){
		ServiceMethodWraper m=ServiceRegistry.getService(request.getServiceName(), request.getVersion(), request.getMethodDecorator());
		RpcResponse resp=new RpcResponse();
		resp.setSerializer(request.getSerializer());
		resp.setRequestId(request.getRequestId());
		if(m==null){
			resp.setResponseCode(ResponseStatus.NOT_FOUND.getCode());
			resp.setResponseDesc(ResponseStatus.NOT_FOUND.getDesc());
		}
		try{
			Object ret=m.getMethod().invoke(m.getService(), request.getArgs());
			resp.setData(ret);
			resp.setResponseCode(ResponseStatus.OK.getCode());
			resp.setResponseDesc(ResponseStatus.OK.getDesc());
		}catch(Exception e){
			logger.error("invoke err,"+request, e);
			resp.setException(e);
			resp.setResponseCode(500);
			resp.setResponseDesc(e.getMessage());
		}
		return resp;
	}
	
}
