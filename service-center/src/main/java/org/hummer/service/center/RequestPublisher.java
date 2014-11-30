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
package org.hummer.service.center;

import io.netty.channel.Channel;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.hummer.api.RpcRequest;
import org.hummer.api.event.Publisher;
import org.hummer.api.event.RequestEvent;
import org.hummer.config.GolbalConfigurationFactory;

import com.higherfrequencytrading.affinity.AffinitySupport;
import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;

public class RequestPublisher implements Publisher<RpcRequest>{
	public static final int BUFFER_SIZE = 128;

	private RingBuffer<RequestEvent>[] ringBuffers;
	private static final int THREAD_NUM=GolbalConfigurationFactory.getInstance().configure().getServerEventChannelNum();
	
	private final ExecutorService executor = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(),new HummerThreadFactory());
	
	private ThreadLocal<Random> random=new ThreadLocal<Random>(){
		protected Random initialValue() {
			return new Random();
		};
	};
	
	@SuppressWarnings("unchecked")
	public RequestPublisher(){
		ringBuffers=new RingBuffer[GolbalConfigurationFactory.getInstance().configure().getServerEventChannelNum()];
		for(int i=0;i<GolbalConfigurationFactory.getInstance().configure().getServerEventChannelNum();i++){
			RingBuffer<RequestEvent> ringBuffer = RingBuffer
					.createMultiProducer(RequestEvent.EVENT_FACTORY, BUFFER_SIZE,
							GolbalConfigurationFactory.getInstance().configure().getServerWaitStrategy());
			SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
			RequestHandler handler=new RequestHandler();
			BatchEventProcessor<RequestEvent> batchEventProcessor = new BatchEventProcessor<RequestEvent>(ringBuffer, sequenceBarrier, handler);
			ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
			ringBuffers[i]=ringBuffer;
			executor.submit(batchEventProcessor);
		}
	}
	
	/**
	 * 发布服务请求消息
	 * @param channel
	 * @param resp
	 */
	public void publish(Channel channel,RpcRequest request){
		RingBuffer<RequestEvent> ringBuffer=ringBuffers[random.get().nextInt(GolbalConfigurationFactory.getInstance().configure().getServerEventChannelNum())];
		long index=ringBuffer.next(1);
		RequestEvent event=ringBuffer.get(index);
		event.setChannel(channel);
		event.setRequest(request);
		ringBuffer.publish(index);
	}
	
	@SuppressWarnings("restriction")
	static class HummerThreadFactory implements ThreadFactory {

		private static final long BASE_AFFINTITY=AffinitySupport.getAffinity();
		private static int PROCESSORS;
		
		static final AtomicInteger poolNumber = new AtomicInteger(1);
	    final ThreadGroup group;
	    final AtomicInteger threadNumber = new AtomicInteger(0);
	    final String namePrefix;
		
		static{
			ScriptEngineManager manager=new ScriptEngineManager();
			ScriptEngine scriptEngine=manager.getEngineByExtension("js");
			Bindings bindings=scriptEngine.createBindings();
			bindings.put("ps", Runtime.getRuntime().availableProcessors());
			String coreUseExp=GolbalConfigurationFactory.getInstance().configure().getServerCoreUse();
			if(coreUseExp!=null){
				try {
					PROCESSORS=(int)Double.parseDouble((String.valueOf(scriptEngine.eval(coreUseExp,bindings))));
				} catch (ScriptException e) {
					PROCESSORS=Runtime.getRuntime().availableProcessors()/2;
				}
			}else{
				PROCESSORS=Runtime.getRuntime().availableProcessors()/2;
			}
		}
		
		
		
		public HummerThreadFactory() {
			SecurityManager s = System.getSecurityManager();
	        group = (s != null)? s.getThreadGroup() :
	                             Thread.currentThread().getThreadGroup();
	        namePrefix = "pool-" +
	                      poolNumber.getAndIncrement() +
	                     "-thread-";
		}



		public Thread newThread(final Runnable r) {
			Thread t=new Thread(group,new Runnable(){

				public void run() {
					try{
						AffinitySupport.setAffinity(1<<(threadNumber.getAndIncrement()%PROCESSORS));
						r.run();
					}finally{
						AffinitySupport.setAffinity(BASE_AFFINTITY);
					}
				}},namePrefix + threadNumber.getAndIncrement(),0);
			if (t.isDaemon())
	            t.setDaemon(false);
	        if (t.getPriority() != Thread.NORM_PRIORITY)
	            t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
	
}
