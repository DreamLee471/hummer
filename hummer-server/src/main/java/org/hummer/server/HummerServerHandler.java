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
package org.hummer.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hummer.api.ContextConstants;
import org.hummer.api.InvocationContext;
import org.hummer.api.Request;
import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.api.event.Publisher;
import org.hummer.api.server.Server;
import org.hummer.remoting.ResponseStatus;
import org.hummer.service.ServiceLocator;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import org.hummer.api.event.Publisher;
import org.hummer.service.ServiceLocator;

public class HummerServerHandler extends ChannelHandlerAdapter{
	
	public static final List<Channel> channels=new CopyOnWriteArrayList<Channel>();
	
	@SuppressWarnings("unchecked")
	private static final Publisher<Request> requestPublisher=ServiceLocator.loadService(Publisher.class, "request");
	
	private Server server;
	
	public HummerServerHandler(Server server) {
		super();
		this.server = server;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		if(server.isPaused()){
			RpcResponse resp=new RpcResponse();
			RpcRequest request=(RpcRequest)msg;
			resp.setSerializer(request.getSerializer());
			resp.setRequestId(request.getRequestId());
			resp.setResponseCode(ResponseStatus.SERVER_PAUSED.getCode());
			resp.setResponseDesc(ResponseStatus.SERVER_PAUSED.getDesc());
			ctx.writeAndFlush(resp);
			return;
		}
		InvocationContext.putValue(ContextConstants.REMOTE_IP, ((InetSocketAddress)ctx.channel().remoteAddress()).getHostName());
		InvocationContext.putValue(ContextConstants.REMOTE_PORT, ((InetSocketAddress)ctx.channel().remoteAddress()).getPort());
		InvocationContext.putValue(ContextConstants.NETTY_CHANNEL, ctx.channel());
		requestPublisher.publish(ctx.channel(), (Request)msg);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		channels.remove(ctx.channel());
	}

}
