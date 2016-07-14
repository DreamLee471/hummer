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
package org.hummer.server.http.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.hummer.api.Request;
import org.hummer.api.RpcRequest;
import org.hummer.api.event.Publisher;
import org.hummer.serialize.SerializerFactory;
import org.hummer.server.http.RequestHandler;
import org.hummer.service.ServiceLocator;

public class RpcRequestHandler implements RequestHandler {
	
	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
	
	@SuppressWarnings("unchecked")
	private static final Publisher<Request> requestPublisher=ServiceLocator.loadService(Publisher.class, "request");

	public void handle(HttpRequest request,ChannelHandlerContext ctx) throws Exception {
		RpcRequest rpcReq=parseRequest(request);
		requestPublisher.publish(ctx.channel(), rpcReq);
	}
	
	
	private RpcRequest parseRequest(HttpRequest request) throws IOException {
		RpcRequest rpcReq=new RpcRequest();
		if(request.getMethod().equals(HttpMethod.GET)){
			QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
			String service=decoderQuery.parameters().get("service").get(0);
			String version=decoderQuery.parameters().get("version").get(0);
			String param=decoderQuery.parameters().get("param").get(0);
			String method=decoderQuery.parameters().get("method").get(0).replace(":", ";");
			byte[] bytes=Base64.decodeBase64(param);
			rpcReq.setArgs((Object[])SerializerFactory.getSerializerByType(1).unSerialize(bytes));
			rpcReq.setServiceName(service);
			rpcReq.setVersion(version);
			rpcReq.setMethodDecorator(method);
		}else if(request.getMethod().equals(HttpMethod.POST)){
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, request);
			InterfaceHttpData serviceData = decoder.getBodyHttpData("service");
			InterfaceHttpData versionData = decoder.getBodyHttpData("version");
			InterfaceHttpData argsData = decoder.getBodyHttpData("args");
			InterfaceHttpData requestIdData = decoder.getBodyHttpData("requestId");
			InterfaceHttpData methodData = decoder.getBodyHttpData("method");
			if(serviceData.getHttpDataType()==HttpDataType.Attribute){
				Attribute attr=(Attribute)serviceData;
				rpcReq.setServiceName(attr.getValue());
			}
			if(versionData.getHttpDataType()==HttpDataType.Attribute){
				Attribute attr=(Attribute)versionData;
				rpcReq.setVersion(attr.getValue());
			}
			
			if(methodData.getHttpDataType()==HttpDataType.Attribute){
				Attribute attr=(Attribute)methodData;
				rpcReq.setMethodDecorator(attr.getValue());
			}
			
			if(argsData!=null && argsData.getHttpDataType()==HttpDataType.Attribute){
				Attribute attr=(Attribute)argsData;
				byte[] bytes=Base64.decodeBase64(attr.getValue());
				rpcReq.setArgs((Object[])SerializerFactory.getSerializerByType(1).unSerialize(bytes));
			}
			
			if(requestIdData !=null && requestIdData.getHttpDataType()==HttpDataType.Attribute){
				Attribute attr=(Attribute)requestIdData;
				rpcReq.setRequestId(Long.valueOf(attr.getValue()));
			}
		}
		return rpcReq;
	}

}
