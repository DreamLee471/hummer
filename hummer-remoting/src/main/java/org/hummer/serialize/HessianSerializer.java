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
package org.hummer.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hummer.api.seralizer.Serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

public class HessianSerializer implements Serializer {
	
	private static final Logger logger=Logger.getLogger(HessianSerializer.class);
	
	public byte[] serializer(Serializable obj) {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		Hessian2Output output=new Hessian2Output(bos);
		try {
			output.writeObject(obj);
			output.flush();
		} catch (IOException e) {
			logger.error("serializer error:"+obj,e);
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

	public Serializable unSerialize(byte[] bytes) throws IOException {
		ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
		Hessian2Input input=new Hessian2Input(bis);
		return (Serializable)input.readObject();
	}

}
