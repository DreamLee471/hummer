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

import java.util.Arrays;
import java.util.Map;

import org.hummer.api.seralizer.Serializer;


public final class RpcRequest implements Request{
	
	/**
	 * 参数的序列化方式
	 */
	private Serializer serializer;
	
	/**
	 * 请求ID
	 */
	private transient long requestId;
	
	/**
	 * 方法的jvm内部表示
	 */
	private String methodDecorator;
	
	/**
	 * 调用的接口
	 */
	private String serviceName;
	
	/**
	 * 调用方法的版本
	 */
	private String version;
	
	/**
	 * 参数
	 */
	private Object[] args;
	
	/**
	 * 请求的附加信息
	 */
	private Map<String,Object> attachment;

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public String getMethodDecorator() {
		return methodDecorator;
	}

	public void setMethodDecorator(String methodDecorator) {
		this.methodDecorator = methodDecorator;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	public Map<String, Object> getAttachment() {
		return attachment;
	}

	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(args);
		result = prime * result
				+ ((methodDecorator == null) ? 0 : methodDecorator.hashCode());
		result = prime * result + (int) (requestId ^ (requestId >>> 32));
		result = prime * result
				+ ((serializer == null) ? 0 : serializer.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcRequest other = (RpcRequest) obj;
		if (!Arrays.equals(args, other.args))
			return false;
		if (methodDecorator == null) {
			if (other.methodDecorator != null)
				return false;
		} else if (!methodDecorator.equals(other.methodDecorator))
			return false;
		if (requestId != other.requestId)
			return false;
		if (serializer == null) {
			if (other.serializer != null)
				return false;
		} else if (!serializer.equals(other.serializer))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "RpcRequest [serializer=" + serializer + ", requestId="
				+ requestId + ", methodDecorator=" + methodDecorator
				+ ", serviceName=" + serviceName + ", version=" + version
				+ ", args=" + Arrays.toString(args) + "]";
	}

}
