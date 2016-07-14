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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.atomic.AtomicBoolean;

import org.hummer.api.server.Server;
import org.hummer.config.GolbalConfigurationFactory;
import org.hummer.remoting.codec.HummerDecoder;
import org.hummer.remoting.codec.HummerEncoder;
import org.hummer.server.http.HttpServer;

public class NettyServer implements Server {

	private ServerBootstrap bootstrap;
	
	private AtomicBoolean started=new AtomicBoolean(false);
	
	private AtomicBoolean inited=new AtomicBoolean(false);
	
	private AtomicBoolean paused = new AtomicBoolean(false);
	
	public void init() {
		if(!inited.compareAndSet(false, true)){
			return;
		}
		inited.set(true);
		bootstrap=new ServerBootstrap();
		EventLoopGroup boss=new NioEventLoopGroup(GolbalConfigurationFactory.getInstance().configure().getServerBossThreads());
		EventLoopGroup worker=new NioEventLoopGroup(GolbalConfigurationFactory.getInstance().configure().getServerWorkerThreads());
		bootstrap.group(boss, worker)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_BACKLOG, 128)
			.option(ChannelOption.SO_KEEPALIVE, true) 
			.channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast("encoder", new HummerEncoder());
					ch.pipeline().addLast("decoder", new HummerDecoder());
					ch.pipeline().addLast("handler", new HummerServerHandler(NettyServer.this));
				}
			});
	}

	public void start() {
		if(!started.compareAndSet(false, true)){
			return;
		}
		started.set(true);
		bootstrap.bind(GolbalConfigurationFactory.getInstance().configure().getServerPort());
		if(GolbalConfigurationFactory.getInstance().configure().isSupportHttp()){
			new Thread(new Runnable() {
				
				public void run() {
					HttpServer server=new HttpServer();
					server.init();
					server.start();
				}
			}, "hummer-http").start();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			public void run() {
				pause();
				try {
					Thread.sleep(100000);
				} catch (InterruptedException e) {
				}
				for(Channel channel:HummerServerHandler.channels){
					channel.close();
				}
			}
		}));
	}

	public void pause() {
		paused.set(true);
	}

	public void stop() {
		for(Channel channel:HummerServerHandler.channels){
			channel.close();
		}
	}

	public boolean isStarted() {
		return started.get();
	}

	public boolean isPaused() {
		return paused.get();
	}

}
