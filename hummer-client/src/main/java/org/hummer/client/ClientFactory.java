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
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hummer.api.client.Client;
import org.hummer.api.client.ClientConfig;
import org.hummer.api.client.HostPort;
import org.hummer.remoting.codec.HummerDecoder;
import org.hummer.util.InitOnce;
import org.hummer.api.client.HostPort;
import org.hummer.remoting.codec.HummerDecoder;

public class ClientFactory {
	
	private static Bootstrap bootstrap;
	
	public static final Map<HostPort, Client> clients=new ConcurrentHashMap<HostPort, Client>();
	
	private static final ScheduledExecutorService executorServices=Executors.newScheduledThreadPool(1);
	
	private static InitOnce<HttpClient> httpClient = InitOnce.init(new Callable<HttpClient>() {

		public HttpClient call() throws Exception {
			return new HttpClient();
		}
	});
	
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
		
		executorServices.scheduleAtFixedRate(new HeartBeatThread(), 0, 1000, TimeUnit.MILLISECONDS);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			public void run() {
				for(Entry<HostPort, Client> client:clients.entrySet()){
					client.getValue().close();
				}
			}
		}));
		
	}
	
	public static Client getClient(String host,int port){
		return getClient(new HostPort(host, port),false);
	}
	
	
	public static Client getClient(ClientConfig config){
		return getClient(config.getHostPort(), config.isHttp());
	}
	
	public static Client getClient(HostPort hostPort,boolean isHttp){
		if(isHttp) return httpClient.get();
		if(clients.get(hostPort)!=null) return clients.get(hostPort);
		ChannelFuture future=bootstrap.connect(hostPort.getHost(), hostPort.getPort());
		Channel channel=future.channel();
		NettyClient client=new NettyClient(channel);
		clients.put(hostPort, client);
		return client;
	}

}
