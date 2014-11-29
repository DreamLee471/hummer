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

import org.hummer.api.exception.HummerConfigException;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class GolbalConfigurationFactory {
	
	private boolean configed=false;
	
	private static GolbalConfigurationFactory instance;
	
	private GolbalConfiguration configuration;
	
	private GolbalConfigurationFactory(){
		configuration=new GolbalConfiguration();
	}
	
	public static GolbalConfigurationFactory getInstance(){
		if(instance==null){
			instance=new GolbalConfigurationFactory();
		}
		return instance;
	}
	
	private void check(){
		if(configed) throw new HummerConfigException("has been configed!");
	}
	
	public GolbalConfigurationFactory serverBossThreads(int threads){
		check();
		configuration.serverBossThreads=threads;
		return this;
	}
	
	
	public GolbalConfigurationFactory serverWorkerThreads(int threads){
		check();
		configuration.serverWorkerThreads=threads;
		return this;
	}
	
	public GolbalConfigurationFactory requestProssorThreads(int threads){
		check();
		configuration.requestProssorThreads=threads;
		return this;
	}
	
	public GolbalConfigurationFactory serverPort(int port){
		check();
		configuration.serverPort=port;
		return this;
	}
	
	public GolbalConfigurationFactory clientWorkerThreads(int threads){
		check();
		configuration.clientWorkerThreads=threads;
		return this;
	}
	
	public GolbalConfigurationFactory responseProssorThreads(int threads){
		check();
		configuration.responseProssorThreads=threads;
		return this;
	}
	
	public GolbalConfigurationFactory configRegistryAddress(String address){
		check();
		configuration.configRegistryAddress=address;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public GolbalConfigurationFactory serverWaitStrategy(String adserverWaitStrategy){
		check();
		try {
			configuration.serverWaitStrategy=(Class<? extends WaitStrategy>)Class.forName(adserverWaitStrategy+"WaitStrategy");
		} catch (Exception e) {
			configuration.serverWaitStrategy=YieldingWaitStrategy.class;
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public GolbalConfigurationFactory clientWaitStrategy(String clientWaitStrategy){
		check();
		try {
			configuration.clientWaitStrategy=(Class<? extends WaitStrategy>)Class.forName(clientWaitStrategy+"WaitStrategy");
		} catch (Exception e) {
			configuration.clientWaitStrategy=YieldingWaitStrategy.class;
		}
		return this;
	}
	
	public GolbalConfigurationFactory serverEventChannelNum(int nums){
		check();
		configuration.serverEventChannelNum=nums;
		return this;
	}
	
	public GolbalConfigurationFactory serverCoreUse(String coreUse){
		check();
		configuration.serverCoreUse=coreUse;
		return this;
	}
	
	public GolbalConfiguration configure(){
		if(!configed) configed=true;
		return configuration;
	}
	
	
	public class GolbalConfiguration{
		private int serverBossThreads=16;
		private int serverWorkerThreads=32;
		
		private int requestProssorThreads=32;
		private int serverPort=6288;
		
		private int clientWorkerThreads=16;
		private int responseProssorThreads=16;
		
		private String configRegistryAddress="localhost:2181";
		
		private Class<? extends WaitStrategy> serverWaitStrategy=YieldingWaitStrategy.class;
		
		private Class<? extends WaitStrategy> clientWaitStrategy=BlockingWaitStrategy.class;
		
		private int serverEventChannelNum=4;
		
		private String serverCoreUse="ps/2";
		
		private GolbalConfiguration(){}
		
		public int getServerBossThreads() {
			return serverBossThreads;
		}
		public int getServerWorkerThreads() {
			return serverWorkerThreads;
		}
		public int getRequestProssorThreads() {
			return requestProssorThreads;
		}
		public int getServerPort() {
			return serverPort;
		}
		public int getClientWorkerThreads() {
			return clientWorkerThreads;
		}
		public int getResponseProssorThreads() {
			return responseProssorThreads;
		}

		public String getConfigRegistryAddress() {
			return configRegistryAddress;
		}

		public WaitStrategy getServerWaitStrategy() {
			try {
				return serverWaitStrategy.newInstance();
			} catch (Exception e) {
				return new YieldingWaitStrategy();
			}
		}

		public WaitStrategy getClientWaitStrategy() {
			try {
				return clientWaitStrategy.newInstance();
			} catch (Exception e) {
				return new YieldingWaitStrategy();
			}
		}

		public int getServerEventChannelNum() {
			return serverEventChannelNum;
		}

		public String getServerCoreUse() {
			return serverCoreUse;
		}
	}

}
