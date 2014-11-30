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
package org.hummer.spring.beans;

import org.hummer.client.conf.ClientMetaData;
import org.hummer.client.proxy.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ReferenceBean implements FactoryBean<Object>,InitializingBean {

	private String service;
	private String version;
	private int timeout;
	private boolean unit;
	private int retry;
	
	private Object target;
	
	
	public void afterPropertiesSet() throws Exception {
		ClientMetaData metadata=new ClientMetaData();
		metadata.setService(service);
		metadata.setVersion(version);
		metadata.setTimeout(timeout);
		metadata.setUnit(unit);
		metadata.setRetrytimes(retry);
		target=ProxyFactory.getProxy(metadata);
	}
	
	public Object getObject() throws Exception {
		return target;
	}

	public Class<?> getObjectType() {
		return target==null?null:target.getClass();
	}

	public boolean isSingleton() {
		return true;
	}

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

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean isUnit() {
		return unit;
	}

	public void setUnit(boolean unit) {
		this.unit = unit;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

}
