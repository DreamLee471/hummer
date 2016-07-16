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
package org.hummer.api.protocol;

import org.hummer.api.exception.ProtocolException;
import org.hummer.api.remoting.ByteBufferWraper;

public interface HummerProtocol {
	
	/**
	 * 对对象进行编码
	 * @param msg
	 * @param byteBuf
	 */
	public void encode(Object msg,ByteBufferWraper byteBuf);
	
	
	/**
	 * 解码
	 * @param byteBuf
	 * @return
	 */
	public Object decode(ByteBufferWraper byteBuf) throws ProtocolException;

}
