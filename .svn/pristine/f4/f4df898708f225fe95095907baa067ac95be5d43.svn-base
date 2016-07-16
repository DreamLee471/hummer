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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hummer.api.client.HostPort;
import org.hummer.config.LoadBanlanceService;

/**
 * 基于饥饿向量进行服务之间的负载均衡。
 * @author dreamlee.lw
 *
 */
public class RobinLoadBalanceService implements LoadBanlanceService {
	
	private static final Logger logger=Logger.getLogger(RobinLoadBalanceService.class);
	
	private Distributions distributions=new Distributions();
	
	private LinkedHashMap<HostPort, Double> weights=new LinkedHashMap<HostPort, Double>();
	
	private LinkedHashMap<HostPort, Double> standByWeights=new LinkedHashMap<HostPort, Double>();
	
	private long sumWs;
	
	public HostPort select(List<String> addresses) {
		List<HostPort> hosts=new ArrayList<HostPort>(addresses.size());
		List<Double> ws=new ArrayList<Double>(addresses.size());
		for(String address:addresses){
			HostPort host=new HostPort(address.split(":")[0], Integer.parseInt(address.split(":")[1]));
			Double weight = weights.get(host);
			hosts.add(host);
			ws.add(weight==null?0:weight);
		}
		return hungry(hosts,ws);
	}

	/**
	 * 饥饿向量算法 
	 * 	max(Wi*Sa - W*Si)<br/>
	 * 其中
	 * <ul>
	 * 	<li> Wi表示当前机器的权重</li>
	 * 	<li> Sa表示总请求数</li>
	 *  <li> W表示所有机器的权重合</li>
	 * 	<li> Si表示当前机器历史请求数</li> 
	 * </ul>
	 * 
	 * @param hosts
	 * @param ws
	 * @return
	 */
	private HostPort hungry(List<HostPort> hosts, List<Double> ws) {
		List<HostPortPair> hungrys=new ArrayList<HostPortPair>(hosts.size());
		long allSeqs=distributions.getAllSeqs();
		for(int i=0;i<hosts.size();i++){
			HostPort host=hosts.get(i);
			Double hungry=ws.get(i) * allSeqs - sumWs * distributions.getDistributions(host);
			hungrys.add(new HostPortPair(host, hungry));
		}
		return hungrys.isEmpty()?null:Collections.max(hungrys).getHost();
	}

	private long sum(Collection<Double> ws) {
		long sum=0;
		for(Double l:ws){
			sum+=l;
		}
		return sum;
	}

	public void registerWeight(HostPort hostPort, double weight) {
		standByWeights.put(hostPort, weight);
		logger.info("register: "+hostPort+","+weight);
	}

	public void rebuild() {
		LinkedHashMap<HostPort, Double> temp=null;
		temp=weights;
		weights=standByWeights;
		standByWeights=temp;
		
		sumWs=sum(weights.values());
	}

	public void serviced(HostPort host) {
		distributions.selected(host);
		distributions.serviced();
	}
	
	
	private static class HostPortPair implements Comparable<HostPortPair>{
		private final HostPort host;
		private final Double hugry;
		
		public HostPortPair(HostPort host, Double hugry) {
			this.host = host;
			this.hugry = hugry;
		}

		public HostPort getHost() {
			return host;
		}

		public Double getHugry() {
			return hugry;
		}

		public int compareTo(HostPortPair o) {
			return (int)(hugry-o.getHugry());
		}
		
	}

}
