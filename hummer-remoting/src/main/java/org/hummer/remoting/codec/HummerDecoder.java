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
package org.hummer.remoting.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import org.hummer.api.protocol.HummerProtocol;
import org.hummer.api.remoting.ByteBufferWraper;
import org.hummer.remoting.NettyByteBuffer;
import org.hummer.service.ServiceLocator;

public class HummerDecoder extends ReplayingDecoder<Object> {

	private static HummerProtocol protocol=ServiceLocator.loadService(HummerProtocol.class, "hummer1.1");
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		ByteBufferWraper buf=new NettyByteBuffer(in);
		out.add(protocol.decode(buf));
	}

}
