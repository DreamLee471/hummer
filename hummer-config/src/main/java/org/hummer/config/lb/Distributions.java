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
package org.hummer.config.lb;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.hummer.api.client.HostPort;

public class Distributions {
	
	private ConcurrentHashMap<HostPort, AtomicLong> serverDistributions=new ConcurrentHashMap<HostPort, AtomicLong>();
	
	private AtomicLong allSeqs=new AtomicLong();
	
	
	public void selected(HostPort host){
		if(!serverDistributions.containsKey(host)){
			serverDistributions.put(host, new AtomicLong(0));
		}
		serverDistributions.get(host).incrementAndGet();
	}
	
	public long getDistributions(HostPort host){
		return serverDistributions.get(host)==null?0:serverDistributions.get(host).longValue();
	}
	
	public void serviced(){
		allSeqs.incrementAndGet();
	}
	
	public long getAllSeqs(){
		return allSeqs.get();
	}

}
