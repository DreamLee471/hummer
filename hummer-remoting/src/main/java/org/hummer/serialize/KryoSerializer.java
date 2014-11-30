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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.hummer.api.seralizer.Serializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializer implements Serializer {
	
	private ThreadLocal<Kryo> kryoInstance=new ThreadLocal<Kryo>(){
		protected Kryo initialValue() {
			Kryo kryo=new Kryo();
			kryo.setReferences(true);  
			kryo.setRegistrationRequired(false);  
			kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
			return kryo;
		};
	};
	

	public byte[] serializer(Serializable obj) {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		Output out=new Output(bos);
		kryoInstance.get().writeClassAndObject(out, obj);
		out.flush();
		return bos.toByteArray();
	}

	public Serializable unSerialize(byte[] bytes) throws IOException {
		Input input=new Input(bytes);
		return (Serializable)kryoInstance.get().readClassAndObject(input);
	}

}
