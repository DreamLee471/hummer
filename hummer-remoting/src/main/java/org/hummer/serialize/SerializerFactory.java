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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hummer.api.seralizer.Serializer;

public class SerializerFactory {
	
	/**
	 * 序列化器定义
	 */
	public static Map<Class<? extends Serializer>,Integer> SERIALIZER_FACTORY_DEFINITION=new ConcurrentHashMap<Class<? extends Serializer>,Integer>();
	
	/**
	 * 序列化器
	 */
	public static Map<Integer,Serializer> SERIALIZER_FACTORY=new ConcurrentHashMap<Integer, Serializer>();
	
	static{
		SERIALIZER_FACTORY_DEFINITION.put(HessianSerializer.class, 1);
		SERIALIZER_FACTORY_DEFINITION.put(KryoSerializer.class, 2);
		
		SERIALIZER_FACTORY.put(1, new HessianSerializer());
		SERIALIZER_FACTORY.put(2, new KryoSerializer());
	}
	
	
	public  static Serializer getSerializerByType(int type){
		return SERIALIZER_FACTORY.get(type); 
	}

}
