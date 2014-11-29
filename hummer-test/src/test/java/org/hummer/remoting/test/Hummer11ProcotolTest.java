package org.hummer.remoting.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.api.exception.ProtocolException;
import org.hummer.api.remoting.ByteBufferWraper;
import org.hummer.remoting.NettyByteBuffer;
import org.hummer.remoting.protocol.Hummer11Protocol;
import org.hummer.serialize.HessianSerializer;

public class Hummer11ProcotolTest extends TestCase {
	
	private RpcRequest request;
	
	@Override
	protected void setUp() throws Exception {
		request=new RpcRequest();
		request.setRequestId(10000L);
		request.setMethodDecorator("calc(Ljava.util.Map;Ljava.util.List)I");
		request.setServiceName("calcService");
		request.setVersion("1.0.0");
		request.setSerializer(new HessianSerializer());
		Map<String,String> map=new HashMap<String, String>();
		map.put("hello", "world");
		List<Integer> list=new ArrayList<Integer>();
		list.add(1);
		request.setArgs(new Object[]{map,list});
	}
	
	public void testEncodeAndDecodeRequest() throws ProtocolException{
		Hummer11Protocol protocol=new Hummer11Protocol();
		ByteBuf buf=Unpooled.buffer(1024);
		ByteBufferWraper wraper=new NettyByteBuffer(buf);
		protocol.encode(request, wraper);
		
		RpcRequest requestTarget=(RpcRequest)protocol.decode(wraper);
		assertEquals(request.getRequestId(), requestTarget.getRequestId());
		assertEquals(request.getMethodDecorator(), requestTarget.getMethodDecorator());
		assertEquals(request.getServiceName(),requestTarget.getServiceName());
		for(int i=0;i<request.getArgs().length;i++){
			assertEquals(request.getArgs()[i], requestTarget.getArgs()[i]);
		}
		assertEquals(request.getVersion(), requestTarget.getVersion());
	}
	
	public void testEncodeAndDecodeResponse() throws ProtocolException{
		Hummer11Protocol protocol=new Hummer11Protocol();
		ByteBuf buf=Unpooled.buffer(1024);
		ByteBufferWraper wraper=new NettyByteBuffer(buf);
		RpcResponse resp=new RpcResponse();
		resp.setRequestId(10000L);
		resp.setResponseCode(200);
		resp.setResponseDesc("ok");
		resp.setSerializer(new HessianSerializer());
		Map<String,Object> m=new HashMap<String,Object>();
		m.put("aa", new ArrayList<Object>());
		m.put("date", new Date());
		resp.setData(m);
		protocol.encode(resp, wraper);
		
		RpcResponse respTarget=(RpcResponse)protocol.decode(wraper);
		assertNotNull(respTarget);
		assertEquals(respTarget.getRequestId(), resp.getRequestId());
		assertEquals(respTarget.getResponseCode(), resp.getResponseCode());
		assertEquals(respTarget.getResponseDesc(), resp.getResponseDesc());
		assertEquals(respTarget.getData(), resp.getData());
		
	}
	
}
