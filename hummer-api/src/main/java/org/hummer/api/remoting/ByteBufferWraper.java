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
package org.hummer.api.remoting;

import java.io.OutputStream;

public interface ByteBufferWraper {
	
	
	public void writeByte(byte b);
	
	public byte readByte();
	
	public void writeInt(int i);
	
	public int readInt();
	
	public void writeLong(long l);
	
	public long readLong();
	
	public char readChar();
	
	public void writeChar(char c);
	
	public void writeBytes(byte[] bytes);
	
	public byte[] readBytes(int start,int len);
	
	public void readBytes(byte[] buffer);
	
	public byte[] toBytes();
	
	public void writeTo(OutputStream os);
	

}
