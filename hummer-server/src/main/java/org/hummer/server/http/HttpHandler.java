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

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.timeout.IdleStateEvent;

import org.apache.log4j.Logger;
import org.hummer.api.RpcResponse;
import org.hummer.remoting.ResponseStatus;
import org.hummer.server.http.handlers.FaviconRequestHandler;
import org.hummer.server.http.handlers.RpcRequestHandler;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
	
	private static final Logger logger=Logger.getLogger(HttpHandler.class);
	
	private static final Map<String, RequestHandler> HANDLERS=new HashMap<String, RequestHandler>();
	
	static{
		HANDLERS.put("/favicon.ico", new FaviconRequestHandler());
		HANDLERS.put("/hummer/openApi", new RpcRequestHandler());
	}
	

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, HttpObject msg)
			throws Exception {
		if(msg instanceof HttpRequest){
			HttpRequest request=(HttpRequest)msg;
			String url=request.getUri().split("\\?")[0];
			RequestHandler handler = HANDLERS.get(url);
			if(handler==null){
				logger.debug("bad request,"+request.getUri());
				RpcResponse resp=new RpcResponse();
				resp.setResponseCode(ResponseStatus.BAD_REQUEST.getCode());
				resp.setResponseDesc(ResponseStatus.BAD_REQUEST.getDesc());
    			ctx.writeAndFlush(resp);
    			return;
			}
			handler.handle(request, ctx);
		}
	}

	
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if(evt instanceof IdleStateEvent){
			switch(((IdleStateEvent) evt).state()){
			case ALL_IDLE:
				ctx.channel().close();
			case WRITER_IDLE:
				;
			case READER_IDLE:
				;
			}
		}
		super.userEventTriggered(ctx, evt);
	}
}
