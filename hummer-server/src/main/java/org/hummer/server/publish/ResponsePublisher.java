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
package org.hummer.server.publish;

import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.hummer.api.RpcResponse;
import org.hummer.api.event.Publisher;
import org.hummer.api.event.ResponseEvent;
import org.hummer.config.GolbalConfigurationFactory;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;

/**
 * 响应消息发布器，由disruptor的处理器进行处理
 * @author dreamlee.lw
 *
 */
public class ResponsePublisher implements Publisher<RpcResponse>{
	
	public static final int BUFFER_SIZE=128;
	
	private final RingBuffer<ResponseEvent> ringBuffer =RingBuffer.
	        createMultiProducer(ResponseEvent.EVENT_FACTORY, BUFFER_SIZE, GolbalConfigurationFactory.getInstance().configure().getClientWaitStrategy());
	
	private final ExecutorService executor = Executors.newFixedThreadPool(16);
	
	private final ResponseHandler handler=new ResponseHandler();
	
	public ResponsePublisher(){
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		BatchEventProcessor<ResponseEvent> batchEventProcessor = new BatchEventProcessor<ResponseEvent>(ringBuffer, sequenceBarrier, handler);
		ringBuffer.addGatingSequences(batchEventProcessor.getSequence());
		executor.submit(batchEventProcessor);
	}
	
	
	/**
	 * 发布响应消息
	 * @param channel
	 * @param resp
	 */
	public void publish(Channel channel,RpcResponse resp){
		long index=ringBuffer.next(1);
		ResponseEvent event=ringBuffer.get(index);
		event.setChannel(channel);
		event.setResponse(resp);
		ringBuffer.publish(index);
	}
	
}
