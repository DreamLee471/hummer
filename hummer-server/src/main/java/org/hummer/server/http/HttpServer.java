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
package org.hummer.server.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLEngine;

import org.hummer.api.RpcResponse;
import org.hummer.api.server.Server;
import org.hummer.config.GolbalConfigurationFactory;
import org.hummer.server.http.ssl.SslContextFactory;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.xstream.XStream;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

public class HttpServer implements Server {
	
	private ServerBootstrap bootstrap;
	
	private AtomicBoolean started=new AtomicBoolean(false);
	
	private AtomicBoolean inited=new AtomicBoolean(false);
	
	private XStream xstream=new XStream();

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
					if (GolbalConfigurationFactory.getInstance().configure().isSsl()) {
			            SSLEngine engine = SslContextFactory.getServerContext().createSSLEngine();
			            engine.setUseClientMode(false);
			            ch.pipeline().addFirst("ssl", new SslHandler(engine));
			        }
					ch.pipeline().addLast("idle", new IdleStateHandler(0,0,180));
			        ch.pipeline().addLast("decoder", new HttpRequestDecoder());
			        ch.pipeline().addLast("encoder", new HttpResponseEncoder());
			        ch.pipeline().addLast("msg2msg", new ChannelHandlerAdapter(){
			        	@Override
			        	public void write(ChannelHandlerContext ctx,
			        			Object msg, ChannelPromise promise)
			        			throws Exception {
			        		if(msg instanceof RpcResponse){
			        			String content="";
			        			String mimeType="text/html; charset=UTF-8";
			        			if("xml".equals(GolbalConfigurationFactory.getInstance().configure().getRespType())){
			        				content=xstream.toXML(msg);
			        				mimeType="text/xml;charset=UTF-8";
			        			}else{
			        				content=JSON.toJSONString(msg);
			        			}
			        			ByteBuf buf=Unpooled.copiedBuffer(content,CharsetUtil.UTF_8);
			        			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
			        			response.headers().set(CONTENT_TYPE, mimeType);
			        	        response.headers().set(CONTENT_LENGTH, buf.readableBytes());
			        			ctx.write(response);
			        			return;
			        		}
			        		super.write(ctx, msg, promise);
			        	}
			        });
			        ch.pipeline().addLast("deflater", new HttpContentCompressor());
			        ch.pipeline().addLast("handler", new HttpHandler());
				}
			});
	}

	public void start() {
		if(!started.compareAndSet(false, true)){
			return;
		}
		started.set(true);
		bootstrap.bind(8080);
	}

	public void pause() {

	}

	public void stop() {

	}

	public boolean isStarted() {
		return false;
	}
	
	public static void main(String[] args) {
	}

	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

}
