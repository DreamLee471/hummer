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
package org.hummer.server.http.handlers;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.io.InputStream;

import org.hummer.server.http.RequestHandler;

public class FaviconRequestHandler implements RequestHandler {

	private static byte[] iconData;

	static {
		InputStream is = FaviconRequestHandler.class.getResourceAsStream("logo.png");
		try {
			iconData = new byte[is.available()];
			is.read(iconData);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void handle(HttpRequest request, ChannelHandlerContext ctx)
			throws Exception {
		if(iconData==null) return;
		ByteBuf buf = Unpooled.copiedBuffer(iconData);
		FullHttpResponse resp=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,buf);
		resp.headers().set(CONTENT_TYPE, "image/png");
        resp.headers().set(CONTENT_LENGTH, buf.readableBytes());
        ctx.writeAndFlush(resp);
	}

}
