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

import org.hummer.api.service.ServiceMetadata;
import org.hummer.service.center.register.ServiceRegistry;
import org.springframework.beans.factory.InitializingBean;

public class ServiceBean implements InitializingBean {

	private String service;
	private String version;
	private Object ref;
	
	public void afterPropertiesSet() throws Exception {
		ServiceMetadata metadata = new ServiceMetadata();
		metadata.setServiceName(service);
		metadata.setTarget(ref);
		metadata.setVersion(service);
		ServiceRegistry.registerService(metadata);
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

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}
}
