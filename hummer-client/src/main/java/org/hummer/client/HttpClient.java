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
package org.hummer.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.hummer.api.Request;
import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.api.client.Client;
import org.hummer.api.client.HostPort;
import org.hummer.api.server.ResponseFuture;
import org.hummer.remoting.ResponseStatus;
import org.hummer.serialize.SerializerFactory;

import com.alibaba.fastjson.JSON;

/**
 * 用HTTP发送请求
 * @author dreamlee.lw
 *
 */
public class HttpClient implements Client {
	
	private CloseableHttpClient httpClient;
	
	private static ThreadLocal<HostPort> HOSTPORT=new ThreadLocal<HostPort>();
	
	public HttpClient(){
		httpClient = HttpClients.createDefault();
	}
	
	public static void register(HostPort hostPort){
		HOSTPORT.set(hostPort);
	}

	@SuppressWarnings("deprecation")
	public void sendRequest(Request request) {
		if(HOSTPORT.get()==null) return;
		RpcRequest req=(RpcRequest)request;
		HttpPost httppost = new HttpPost("http://"+HOSTPORT.get().getHost()+":"+HOSTPORT.get().getPort()+"/hummer/openApi");
		List<NameValuePair> nvps = new ArrayList <NameValuePair>();  
		BasicNameValuePair serviceName=new BasicNameValuePair("service", req.getServiceName());
		BasicNameValuePair version=new BasicNameValuePair("version", req.getVersion());
		BasicNameValuePair method=new BasicNameValuePair("method", req.getMethodDecorator().replace(";", ":"));
		nvps.add(serviceName);
		nvps.add(version);
		nvps.add(method);
		if(req.getArgs()!=null){
			byte[] bytes = SerializerFactory.getSerializerByType(1).serializer(req.getArgs());
			BasicNameValuePair param=new BasicNameValuePair("param", Base64.encodeBase64String(bytes));
			nvps.add(param);
		}
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			CloseableHttpResponse httpresp = httpClient.execute(httppost);
			if(httpresp.getStatusLine().getStatusCode()==200){
				String body = EntityUtils.toString(httpresp.getEntity());  
				RpcResponse resp = JSON.parseObject(body, RpcResponse.class);
				ResponseFuture future = ResponseFuture.RESPONSE_FUTURES.get(req.getRequestId());
				if(future!=null){
					future.onResponse(resp);
				}
			}else if(httpresp.getStatusLine().getStatusCode()==404){
				RpcResponse resp=new RpcResponse();
				resp.setRequestId(req.getRequestId());
				resp.setResponseCode(ResponseStatus.NOT_FOUND.getCode());
				resp.setResponseDesc(ResponseStatus.NOT_FOUND.getDesc());
				ResponseFuture future = ResponseFuture.RESPONSE_FUTURES.get(req.getRequestId());
				if(future!=null){
					future.onResponse(resp);
				}
			}
		} catch (Exception e) {
			RpcResponse resp=new RpcResponse();
			resp.setRequestId(req.getRequestId());
			resp.setException(e);
			resp.setResponseCode(ResponseStatus.SERVER_ERROR.getCode());
			resp.setResponseDesc(ResponseStatus.SERVER_ERROR.getDesc());
			ResponseFuture future = ResponseFuture.RESPONSE_FUTURES.get(req.getRequestId());
			if(future!=null){
				future.onResponse(resp);
			}
		}
	}

	public void close() {
		if(httpClient!=null){
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
