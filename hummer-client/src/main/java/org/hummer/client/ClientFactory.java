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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hummer.api.client.Client;
import org.hummer.remoting.codec.HummerDecoder;

public class ClientFactory {
	
	private static Bootstrap bootstrap;
	
	public static final Map<String, Client> clients=new ConcurrentHashMap<String, Client>();
	
	static{
		bootstrap=new Bootstrap();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		bootstrap.group(workerGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
//				ch.pipeline().addLast("encoder", new HummerEncoder());
				ch.pipeline().addLast("decoder", new HummerDecoder());
				ch.pipeline().addLast("handler", new ClientHandler());
			}
		});
	}
	
	
	public static Client getClent(String host,int port){
		if(clients.get(host+":"+port)!=null) return clients.get(host+":"+port);
		ChannelFuture future=bootstrap.connect(host, port);
		Channel channel=future.channel();
		NettyClient client=new NettyClient(channel);
		clients.put(host+":"+port, client);
		return client;
	}

}
