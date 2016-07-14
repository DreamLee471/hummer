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
package org.hummer.api;

import java.util.Date;

public final class HeartBeatResponse implements Response{
	
	private final String host;
	private final int port;
	
	/**
	 * 请求的时间戳
	 */
	private final long requestTimestamp;
	
	/**
	 * 响应的时间戳（用于计划客户端与服务器之间的网络情况）
	 */
	private final long responseTimestamp;
	
	private final long systemLoad;
	
	
	public HeartBeatResponse(String host, int port, long requestTimestamp,
			long responseTimestamp,long systemLoad) {
		this.host = host;
		this.port = port;
		this.requestTimestamp = requestTimestamp;
		this.responseTimestamp = responseTimestamp;
		this.systemLoad=systemLoad;
	}
	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}
	public long getRequestTimestamp() {
		return requestTimestamp;
	}
	public long getResponseTimestamp() {
		return responseTimestamp;
	}
	public long getSystemLoad() {
		return systemLoad;
	}
	public static HeartBeatResponse create(HeartBeatRequest request,long systemload){
		return new HeartBeatResponse(request.getHost(), request.getPort(), request.getTimestamp(),new Date().getTime(),systemload);
	}
}
