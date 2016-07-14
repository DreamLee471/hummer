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
package org.hummer.client;

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.hummer.api.HeartBeatRequest;
import org.hummer.api.HeartBeatResponse;
import org.hummer.api.client.Client;
import org.hummer.api.client.HostPort;
import org.hummer.api.server.ResponseFuture;
import org.hummer.config.LoadBanlanceService;
import org.hummer.service.ServiceLocator;

/**
 * 心跳检测线程
 * @author dreamlee.lw
 *
 */
public class HeartBeatThread implements Runnable {
	
	private static final Logger logger=Logger.getLogger(HeartBeatThread.class);
	
	private static LoadBanlanceService lbService=ServiceLocator.loadService(LoadBanlanceService.class, "robin");
	
	private static ConcurrentHashMap<HostPort, AtomicInteger> badRequests=new ConcurrentHashMap<HostPort, AtomicInteger>();
	
	private ExecutorService executorService=Executors.newSingleThreadExecutor();

	public void run() {
		final AtomicInteger counts=new AtomicInteger();
		final CopyOnWriteArrayList<HostPortLoad> loads=new CopyOnWriteArrayList<HostPortLoad>();
		for(final Entry<HostPort, Client> entry:ClientFactory.clients.entrySet()){
			final ResponseFuture future=new ResponseFuture();
			ResponseFuture.HEART_BEAT_FUTURES.put(entry.getKey(), future);
			entry.getValue().sendRequest(new HeartBeatRequest(entry.getKey().getHost(), entry.getKey().getPort(), new Date().getTime()));
			counts.incrementAndGet();
			executorService.submit(new Runnable() {
				
				public void run() {
					try {
						HeartBeatResponse response = (HeartBeatResponse)future.get(3000, TimeUnit.MILLISECONDS);
						if(response==null){
							processTimeout();
							return;
						}
						badRequests.put(entry.getKey(), new AtomicInteger(0));
						long ttl=response.getResponseTimestamp()-response.getRequestTimestamp();
						long load=response.getSystemLoad();
						//计算总负载 网络*3 + 系统负载*2
						long totalLoad=ttl*3+load*2;
						loads.add(new HostPortLoad(entry.getKey(), totalLoad));
					} catch (Exception e) {
						logger.error("heart beat error", e);
						processTimeout();
					}finally{
						if(counts.decrementAndGet()==0){
							long sumLoads=sum(loads);
							for(HostPortLoad l:loads){
								long load=l.getLoad();
								double weight=Long.valueOf(sumLoads).doubleValue()/Long.valueOf(load).doubleValue();
								lbService.registerWeight(l.getHost(), weight);
							}
							lbService.rebuild();
						}
					}
				}

				/**
				 * 处理超时
				 */
				private void processTimeout() {
					//timeout
					if(!badRequests.containsKey(entry.getKey())){
						badRequests.put(entry.getKey(), new AtomicInteger(0));
					}
					//连续三个心跳包无响应，则从列表中移除机器
					if(badRequests.get(entry.getKey()).incrementAndGet()==3){
						if(ClientFactory.clients.containsKey(entry.getKey())){
							/*zookeeper中还存在，说明存下以下两种情况：
							1、zookeeper挂了，新的列表未推送过来
							2、这台机器没有挂，只是由于某些原因暂时失联
							基于以上的原因，不应该直接将机器移除了，而是放入standBy队列
							*/
							//TODO 加入standBy列表中。
						}
					}
				}
			});
		}
	}
	
	
	
	
	private long sum(List<HostPortLoad> loads){
		long sum=0L;
		for(HostPortLoad l:loads){
			sum+=l.getLoad();
		}
		return sum;
	}
	
	private static final class HostPortLoad{
		private HostPort host;
		private long load;
		
		public HostPortLoad(HostPort host, long load) {
			this.host = host;
			this.load = load;
		}

		public HostPort getHost() {
			return host;
		}

		public long getLoad() {
			return load;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((host == null) ? 0 : host.hashCode());
			result = prime * result + (int) (load ^ (load >>> 32));
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
			HostPortLoad other = (HostPortLoad) obj;
			if (host == null) {
				if (other.host != null)
					return false;
			} else if (!host.equals(other.host))
				return false;
			if (load != other.load)
				return false;
			return true;
		}
	}

}
