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
package org.hummer.config;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class HummerAddressService implements AddressService {
	
	private ConcurrentHashMap<String,ConcurrentHashMap<String,CopyOnWriteArrayList<String>>> methodHolder=new ConcurrentHashMap<String, ConcurrentHashMap<String,CopyOnWriteArrayList<String>>>();
	
	private ConcurrentHashMap<String,CopyOnWriteArrayList<String>> serviceHolder=new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();
	
	private ConcurrentHashMap<String,CopyOnWriteArrayList<String>> targetAddressCache=new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();
	
	private CopyOnWriteArrayList<String> watched=new CopyOnWriteArrayList<String>();
	
	private ConfigClient configClient=new ConfigClient();

	public List<String> getTargetAddress(String service, String version, String method) {
		if(targetAddressCache.containsKey(service+"$"+version+"$"+method)){
			return targetAddressCache.get(service+"$"+version+"$"+method);
		}
		
		CopyOnWriteArrayList<String> targetAddress=null;
		
		if(methodHolder.containsKey(service+"$"+version)){
			targetAddress=methodHolder.get(service+"$"+version).get(method);
		}
		
		if(targetAddress==null){
			targetAddress=serviceHolder.get(service+"$"+version);
		}
		
		if(targetAddress!=null){
			targetAddressCache.put(service+"$"+version+"$"+method, new CopyOnWriteArrayList<String>(targetAddress));
		}
		return targetAddress;
	}

	public void registerTarget(String service, String version, String host) {
		serviceHolder.putIfAbsent(service+"$"+version, new CopyOnWriteArrayList<String>());
		CopyOnWriteArrayList<String> list = serviceHolder.get(service+"$"+version);
		list.addIfAbsent(host);
		
		Iterator<Entry<String, CopyOnWriteArrayList<String>>> iterator = targetAddressCache.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, CopyOnWriteArrayList<String>> entry=iterator.next();
			if(entry.getKey().startsWith(service+"$"+version)){
				iterator.remove();
			}
		}
	}
	
	public void registerTarget(String service, String version, String method,
			String host) {
		methodHolder.putIfAbsent(service+"$"+version, new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>());
		ConcurrentHashMap<String, CopyOnWriteArrayList<String>> serviceMap=methodHolder.get(service+"$"+version);
		serviceMap.putIfAbsent(method, new CopyOnWriteArrayList<String>());
		CopyOnWriteArrayList<String> list=serviceMap.get(method);
		list.addIfAbsent(host);
		
		targetAddressCache.remove(service+"$"+version+"$"+method);
	}

	public void deleteAddress(String host) {
		
	}

	public void watch(final String service, final String version) {
		if(watched.contains(service+"$"+version)){
			return;
		}
		
		//初始化一次
		List<String> list = configClient.getChildConfig(service, version);
		for(String host:list){
			registerTarget(service, version, host);
		}
		
		configClient.watch(service, version, new ConfigWatcher() {
			
			@SuppressWarnings("unchecked")
			public void onEvent(WatchEvent evt) {
				Object obj=evt.getArg();
				if(obj instanceof Collection){
					Collection<String> collection=(Collection<String>)obj;
					if(collection == null || collection.isEmpty()){
						//防空判断，如果是空的，则保护原有的地址
						return;
					}
					for(Iterator<String> iter=collection.iterator();iter.hasNext();){
						registerTarget(service, version, iter.next());
					}
				}else{
					registerTarget(service, version, (String)obj);
				}
			}
		});
		watched.add(service+"$"+version);
	}

}
