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
package org.hummer.remoting;

import java.io.OutputStream;

import io.netty.buffer.ByteBuf;

import org.hummer.api.remoting.ByteBufferWraper;

public class NettyByteBuffer implements ByteBufferWraper {

	private ByteBuf buf;
	
	public NettyByteBuffer(ByteBuf buf) {
		this.buf = buf;
	}

	public void writeByte(byte b) {
		buf.writeByte(b);
	}

	public byte readByte() {
		return buf.readByte();
	}

	public char readChar() {
		return buf.readChar();
	}

	public void writeChar(char c) {
		buf.writeChar(c);
	}

	public void writeBytes(byte[] bytes) {
		buf.writeBytes(bytes);
	}

	public byte[] readBytes(int start, int len) {
		byte[] bytes=new byte[len];
		buf.readBytes(bytes, start, len);
		return bytes;
	}

	public byte[] toBytes() {
		return null;
	}

	public void writeTo(OutputStream os) {
	}

	public void writeLong(long l) {
		buf.writeLong(l);
	}

	public long readLong() {
		return buf.readLong();
	}

	public void readBytes(byte[] buffer) {
		buf.readBytes(buffer);
	}

	public void writeInt(int i) {
		buf.writeInt(i);
	}

	public int readInt() {
		return buf.readInt();
	}

}
