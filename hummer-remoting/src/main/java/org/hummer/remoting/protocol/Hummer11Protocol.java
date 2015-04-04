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
package org.hummer.remoting.protocol;

import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hummer.api.HeartBeatRequest;
import org.hummer.api.HeartBeatResponse;
import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.api.exception.ProtocolException;
import org.hummer.api.protocol.HummerProtocol;
import org.hummer.api.remoting.ByteBufferWraper;
import org.hummer.api.seralizer.Serializer;
import org.hummer.serialize.SerializerFactory;

public class Hummer11Protocol implements HummerProtocol{
	
	private static final Logger logger=Logger.getLogger(Hummer11Protocol.class);
	
	public static final int VERSION=1;
	
	public static final int REQUEST=1;
	public static final int RESPONSE=2;
	public static final int HEART_BEAT_REQUEST=3;
	public static final int HEART_BEAT_RESPONSE=4;
	
	public void encode(Object msg, ByteBufferWraper byteBuf) {
		if(msg instanceof RpcRequest){
			encodeRequest((RpcRequest)msg,byteBuf);
		}else if(msg instanceof RpcResponse){
			encodeResponse((RpcResponse)msg,byteBuf);
		}else if(msg instanceof HeartBeatRequest){
			encodeHeartBeatRequest((HeartBeatRequest)msg,byteBuf);
		}else if(msg instanceof HeartBeatResponse){
			encodeHeartBeatResponse((HeartBeatResponse)msg,byteBuf);
		}
	}

	private void encodeHeartBeatResponse(HeartBeatResponse msg,
			ByteBufferWraper byteBuf) {
		byteBuf.writeByte((byte)HEART_BEAT_RESPONSE);
		byteBuf.writeInt(msg.getHost().length());
		byteBuf.writeBytes(msg.getHost().getBytes());
		byteBuf.writeInt(msg.getPort());
		byteBuf.writeLong(msg.getRequestTimestamp());
		byteBuf.writeLong(msg.getResponseTimestamp());
	}

	private void encodeHeartBeatRequest(HeartBeatRequest msg,
			ByteBufferWraper byteBuf) {
		byteBuf.writeByte((byte)HEART_BEAT_REQUEST);
		byteBuf.writeInt(msg.getHost().length());
		byteBuf.writeBytes(msg.getHost().getBytes());
		byteBuf.writeInt(msg.getPort());
		byteBuf.writeLong(msg.getTimestamp());
	}

	private void encodeResponse(RpcResponse resp, ByteBufferWraper byteBuf) {
		logger.info("start to encode response");
		Serializer serializer=resp.getSerializer();
		byte[] data=serializer.serializer((Serializable)resp.getData());
		byteBuf.writeByte((byte)RESPONSE);
		byteBuf.writeByte((byte)VERSION);
		byteBuf.writeLong(resp.getRequestId());
		byteBuf.writeByte(SerializerFactory.SERIALIZER_FACTORY_DEFINITION.get(serializer.getClass()).byteValue());
		//写入是否压缩的标志位
		byteBuf.writeByte((byte)0);
		//5个保留位
		byteBuf.writeBytes(new byte[]{0,0,0,0,0});
		byteBuf.writeInt(resp.getResponseCode());
		//响应长度及内容
		byteBuf.writeInt(resp.getResponseDesc().length());
		byteBuf.writeBytes(resp.getResponseDesc().getBytes());
		if(resp.getException()!=null){
			byteBuf.writeByte((byte)1);
			byte[] exceptionBytes=serializer.serializer(resp.getException());
			byteBuf.writeInt(exceptionBytes.length);
			byteBuf.writeBytes(exceptionBytes);
		}else{
			byteBuf.writeByte((byte)0);
			byteBuf.writeInt(data.length);
			byteBuf.writeBytes(data);
		}
	}

	private void encodeRequest(RpcRequest request,ByteBufferWraper byteBuf) {
		logger.info("start to encode request");
		Serializer serializer=request.getSerializer();
		byte[] data=serializer.serializer(request.getArgs());
		byteBuf.writeByte((byte)REQUEST);
		byteBuf.writeByte((byte)VERSION);
		byteBuf.writeLong(request.getRequestId());
		//写入序列化方式
		byteBuf.writeByte(SerializerFactory.SERIALIZER_FACTORY_DEFINITION.get(serializer.getClass()).byteValue());
		//写入是否压缩的标志位
		byteBuf.writeByte((byte)0);
		//5个保留位
		byteBuf.writeBytes(new byte[]{0,0,0,0,0});
		//写入服务名长度及数据
		byte[] serviceBytes=request.getServiceName().getBytes();
		byteBuf.writeByte((byte)serviceBytes.length);
		byteBuf.writeBytes(serviceBytes);
		//写入版本及数据
		byte[] versionBytes=request.getVersion()==null?"1.0".getBytes():request.getVersion().getBytes();
		byteBuf.writeByte((byte)versionBytes.length);
		byteBuf.writeBytes(versionBytes);
		//写入方法描述长度及数据
		byte[] methodBytes=request.getMethodDecorator().getBytes();
		byteBuf.writeByte((byte)methodBytes.length);
		byteBuf.writeBytes(methodBytes);
		//写入参数长度及数据
		byteBuf.writeInt(data.length);
		byteBuf.writeBytes(data);
		logger.info("end encode");
	}

	public Object decode(ByteBufferWraper byteBuf) throws ProtocolException{
		byte type=byteBuf.readByte();
		switch(type){
		case REQUEST:
			return decodeRequest(byteBuf);
		case RESPONSE:
			return decodeResponse(byteBuf);
		case HEART_BEAT_REQUEST:
			return decodeHeartRequest(byteBuf);
		case HEART_BEAT_RESPONSE:
			return decodeHeartResponse(byteBuf);
		}
		throw new ProtocolException("错误的协议");
	}

	private Object decodeHeartRequest(ByteBufferWraper byteBuf) {
		int hostLength=byteBuf.readInt();
		byte[] hostBytes=new byte[hostLength];
		byteBuf.readBytes(hostBytes);
		String host=new String(hostBytes);
		int port=byteBuf.readInt();
		long timestamp=byteBuf.readLong();
		return new HeartBeatRequest(host,port,timestamp);
	}

	private Object decodeHeartResponse(ByteBufferWraper byteBuf) {
		int hostLength=byteBuf.readInt();
		byte[] hostBytes=new byte[hostLength];
		byteBuf.readBytes(hostBytes);
		String host=new String(hostBytes);
		int port=byteBuf.readInt();
		long requestTimestamp=byteBuf.readLong();
		long responseTimestamp=byteBuf.readLong();
		return new HeartBeatResponse(host, port, requestTimestamp, responseTimestamp);
	}

	private Object decodeResponse(ByteBufferWraper byteBuf) {
		logger.info("start to decode");
		RpcResponse resp=new RpcResponse();
		byte protocolVersion=byteBuf.readByte();
		if(VERSION!=protocolVersion){
			throw new RuntimeException("错误的版本号");
		}
		long requestId=byteBuf.readLong();
		resp.setRequestId(requestId);
		byte serializerType=byteBuf.readByte();
		resp.setSerializer(SerializerFactory.getSerializerByType(serializerType));
		byteBuf.readByte();
		
		//读出5个保留位
		byteBuf.readByte();
		byteBuf.readByte();
		byteBuf.readByte();
		byteBuf.readByte();
		byteBuf.readByte();
		
		int respCode=byteBuf.readInt();
		resp.setResponseCode(respCode);
		int respDescLength=byteBuf.readInt();
		byte[] descBytes=new byte[respDescLength];
		byteBuf.readBytes(descBytes);
		resp.setResponseDesc(new String(descBytes));
		//异常还是数据
		byte exOrData=byteBuf.readByte();
		if(exOrData==1){
			int exLength=byteBuf.readInt();
			byte[] exBytes=new byte[exLength];
			byteBuf.readBytes(exBytes);
			try {
				resp.setException((Exception)resp.getSerializer().unSerialize(exBytes));
			} catch (IOException e) {
				logger.error("unSerializer exception error", e);
				e.printStackTrace();
			}
		}else{
			int dataLength=byteBuf.readInt();
			byte[] dataBytes=new byte[dataLength];
			byteBuf.readBytes(dataBytes);
			try {
				resp.setData(resp.getSerializer().unSerialize(dataBytes));
			} catch (IOException e) {
				logger.error("unSerializer data error", e);
				e.printStackTrace();
			}
		}
		return resp;
	}
	
	private Object decodeRequest(ByteBufferWraper byteBuf) {
		RpcRequest request=new RpcRequest();
		//版本号
		byte protocolVersion=byteBuf.readByte();
		if(VERSION!=protocolVersion){
			throw new RuntimeException("错误的版本号");
		}
		long requestId=byteBuf.readLong();
		request.setRequestId(requestId);
		byte serializerType=byteBuf.readByte();
		request.setSerializer(SerializerFactory.getSerializerByType(serializerType));
		byteBuf.readByte();
		
		//读出5个保留位
		byteBuf.readByte();
		byteBuf.readByte();
		byteBuf.readByte();
		byteBuf.readByte();
		byteBuf.readByte();
		
		//读取服务名
		int serviceLen=byteBuf.readByte();
		byte[] serviceBytes=new byte[serviceLen];
		byteBuf.readBytes(serviceBytes);
		request.setServiceName(new String(serviceBytes));
		
		//读取版本
		int versionLen=byteBuf.readByte();
		byte[] versionBytes=new byte[versionLen];
		byteBuf.readBytes(versionBytes);
		request.setVersion(new String(versionBytes));
		
		//读取方法描述
		int methodLen=byteBuf.readByte();
		byte[] methodBytes=new byte[methodLen];
		byteBuf.readBytes(methodBytes);
		request.setMethodDecorator(new String(methodBytes));
		
		//读取参数
		int argsLen=byteBuf.readInt();
		byte[] argsBytes=new byte[argsLen];
		byteBuf.readBytes(argsBytes);
		try {
			request.setArgs((Object[])request.getSerializer().unSerialize(argsBytes));
		} catch (IOException e) {
			logger.error("unSerializer error("+request.getServiceName()+"["+request.getMethodDecorator()+"])", e);
			e.printStackTrace();
		}
		return request;
	}

}
