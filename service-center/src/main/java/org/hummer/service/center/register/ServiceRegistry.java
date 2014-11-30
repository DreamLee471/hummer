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
package org.hummer.service.center.register;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hummer.api.service.ServiceMetadata;
import org.hummer.config.ConfigCommetService;
import org.hummer.server.NettyServer;
import org.hummer.service.ServiceLocator;
import org.hummer.util.HummerUtils;

public class ServiceRegistry {

	/**
	 * 服务定义
	 */
	private static Map<String, Class<?>> SERVICE_DEFINITIONS = new ConcurrentHashMap<String, Class<?>>(32);
	
	/**
	 * 服务注册
	 */
	private static ConfigCommetService configService=ServiceLocator.loadService(ConfigCommetService.class);

	/**
	 * 服务方法定义
	 */
	private static Map<String, Map<String, ServiceMethodWraper>> SERVICES = new ConcurrentHashMap<String, Map<String, ServiceMethodWraper>>(
			256);
	
	private static NettyServer server=new NettyServer();

	public static void registerService(ServiceMetadata metadata) {
		String service = metadata.getServiceName();
		String version = metadata.getVersion() == null ? "1.0.0" : metadata
				.getVersion();
		try {
			Class<?> clazz = Class.forName(metadata.getServiceName());
			SERVICE_DEFINITIONS.put(service + ":" + version, clazz);
			if (SERVICES.get(service + ":" + version) == null) {
				SERVICES.put(service + ":" + version,
						new ConcurrentHashMap<String, ServiceMethodWraper>());
			}
			Map<String, ServiceMethodWraper> methodMap = SERVICES.get(service
					+ ":" + version);
			for (Method m : clazz.getMethods()) {
				String methodName = HummerUtils.getMethodDescriptor(m);
				methodMap.put(methodName,
						new ServiceMethodWraper(metadata.getTarget(), m));
			}
			configService.subscribe(service, version);
			if(!server.isStarted()){
				server.init();
				server.start();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void removeService(ServiceMetadata metadata){
		String service=metadata.getServiceName();
		String version=metadata.getVersion();
		SERVICE_DEFINITIONS.remove(service+":"+version);
		SERVICES.remove(service+":"+version);
		configService.remove(service, version);
	}
	
	/**
	 * 
	 * @param name
	 * @param version
	 * @param methodName
	 * @return
	 */
	public static ServiceMethodWraper getService(String name, String version,
			String methodName) {
		if (SERVICES.get(name + ":" + version) == null)
			return null;
		return SERVICES.get(name + ":" + version).get(methodName);
	}

	public static class ServiceMethodWraper {
		private Object service;
		private Method method;

		public ServiceMethodWraper(Object service, Method method) {
			this.service = service;
			this.method = method;
		}

		public Object getService() {
			return service;
		}

		public void setService(Object service) {
			this.service = service;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}
	}

}
