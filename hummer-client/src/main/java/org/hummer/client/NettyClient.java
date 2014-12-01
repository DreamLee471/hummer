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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import org.hummer.api.RpcRequest;
import org.hummer.api.client.Client;
import org.hummer.api.protocol.HummerProtocol;
import org.hummer.remoting.NettyByteBuffer;
import org.hummer.service.ServiceLocator;

public class NettyClient implements Client {
	
	private Channel channel;
	
	HummerProtocol protocol=ServiceLocator.loadService(HummerProtocol.class, "hummer1.1");

	public NettyClient(Channel channel) {
		this.channel = channel;
	}

	public void sendRequest(RpcRequest request) {
		ByteBuf buf=Unpooled.buffer(1024);
		protocol.encode(request, new NettyByteBuffer(buf));
		channel.writeAndFlush(buf);
	}

	public void close() {
		channel.close();
	}

}
