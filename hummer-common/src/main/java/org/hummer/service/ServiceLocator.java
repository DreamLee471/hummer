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
package org.hummer.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class ServiceLocator {
	
	private final static Map<Class<?>,Map<String,Object>> SERVICE_CACHE=new ConcurrentHashMap<Class<?>, Map<String,Object>>();
	public final static String SERVICE_PATH_PREFIX="META-INF/hummer/";
	private static Logger logger=Logger.getLogger(ServiceLocator.class);
	
	private static final Object latcher=new Object();
	
	/**
	 * 加载服务
	 * @param c 服务类型
	 * @param name 服务注册的名称
	 * @return
	 */
	public static <T> T loadService(Class<T> c,String name){
		if(SERVICE_CACHE.get(c)!=null){
			return c.cast(SERVICE_CACHE.get(c).get(name));
		}
		synchronized (latcher) {
			if(SERVICE_CACHE.get(c)==null){
				loadServiceInternal(c,name);
			}
		}
		if(SERVICE_CACHE.get(c)!=null){
			return c.cast(SERVICE_CACHE.get(c).get(name));
		}
		return null;
	}
	
	/**
	 * 加载默认的服务
	 * @param c 服务类型
	 * @return
	 */
	public static <T> T loadService(Class<T> c){
		return loadService(c, "default");
	}
	
	
	private static void loadServiceInternal(Class<?> c,String name){
		ClassLoader loader=ServiceLocator.class.getClassLoader();
		try {
			Enumeration<URL> enumeration=loader.getResources(SERVICE_PATH_PREFIX+c.getName());
			Map<String,Object> services=new HashMap<String, Object>();
			while(enumeration.hasMoreElements()){
				URL url=enumeration.nextElement();
				InputStream is=url.openStream();
				BufferedReader reader=new BufferedReader(new InputStreamReader(is));
				String line=null;
				while((line=reader.readLine())!=null){
					String[] args=line.split("=");
					if(args.length==2){
						logger.info("init service("+c.getName()+"["+args[0]+"]) use "+args[1]);
						if(name.equals(args[0])){
							services.put(args[0], Class.forName(args[1]).newInstance());
						}
					}
				}
				is.close();
			}
			if(SERVICE_CACHE.get(c)==null){
				SERVICE_CACHE.put(c, services);
			}else{
				SERVICE_CACHE.get(c).putAll(services);
			}
		} catch (Exception e) {
			logger.error("load service error", e);
		}
	}

}
