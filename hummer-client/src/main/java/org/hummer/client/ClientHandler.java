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

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.hummer.api.RpcResponse;
import org.hummer.api.event.Publisher;
import org.hummer.service.ServiceLocator;

public class ClientHandler extends ChannelHandlerAdapter{
	
	@SuppressWarnings("unchecked")
	private Publisher<RpcResponse> responsePublisher=ServiceLocator.loadService(Publisher.class, "response");
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof RpcResponse){
			responsePublisher.publish(ctx.channel(), (RpcResponse)msg);
		}
	}
	
}
