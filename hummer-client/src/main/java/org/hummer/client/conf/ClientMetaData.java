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
package org.hummer.client.conf;

public class ClientMetaData {
	
	public static interface InvokeTypes{
		public static final int RPC=0;	
		public static final int HTTP=1;
		public static final int HTTPS=2;
	}
	
	/**
	 * 服务名
	 */
	private String service;
	
	/**
	 * 服务版本
	 */
	private String version;
	
	/**
	 * 超时时间
	 */
	private long timeout=3000;
	
	/**
	 * 重试次数（500错误不重试）
	 */
	private int retrytimes;
	
	/**
	 * 是否单元化
	 */
	private boolean unit;
	
	/**
	 * 设置targetUrl时将采用直连，不走注册中心
	 */
	private String targetUrl;
	
	/**
	 * 多个targetUrl,随机路由
	 */
	private String targetUrls;
	
	private int invokeType;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public int getRetrytimes() {
		return retrytimes;
	}

	public void setRetrytimes(int retrytimes) {
		this.retrytimes = retrytimes;
	}

	public boolean isUnit() {
		return unit;
	}

	public void setUnit(boolean unit) {
		this.unit = unit;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTargetUrls() {
		return targetUrls;
	}

	public void setTargetUrls(String targetUrls) {
		this.targetUrls = targetUrls;
	}

	public int getInvokeType() {
		return invokeType;
	}

	public void setInvokeType(int invokeType) {
		this.invokeType = invokeType;
	}
	
}
