package org.hummer.service.test;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressivePromise;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.hummer.api.RpcRequest;
import org.hummer.api.RpcResponse;
import org.hummer.api.event.Publisher;
import org.hummer.api.service.ServiceMetadata;
import org.hummer.serialize.HessianSerializer;
import org.hummer.service.ServiceLocator;
import org.hummer.service.center.ServiceInvoker;
import org.hummer.service.center.register.ServiceRegistry;
import org.hummer.util.HummerUtils;

public class ServiceInvokerTest extends TestCase{
	
	RpcRequest request=new RpcRequest();
	
	@Override
	protected void setUp() throws Exception {
		IHello hello=new HelloService();
		ServiceMetadata metadata=new ServiceMetadata();
		metadata.setServiceName(IHello.class.getName());
		metadata.setTarget(hello);
		metadata.setVersion("1.0.0");
		
		ServiceRegistry.registerService(metadata);
		
		request.setRequestId(1000L);
		request.setServiceName(IHello.class.getName());
		request.setVersion("1.0.0");
		request.setMethodDecorator(HummerUtils.getMethodDescriptor(IHello.class.getDeclaredMethod("sayHello", new Class[]{String.class})));
		request.setArgs(new Object[]{"world"});
		request.setSerializer(new HessianSerializer());
	}
	
	public void testServiceRegister() throws SecurityException, NoSuchMethodException{
		RpcResponse resp=ServiceInvoker.getInstance().invoke(request);
		assertEquals("hello,world", resp.getData());
	}
	
	@SuppressWarnings("unchecked")
	public void testPublisher() throws IOException, InterruptedException{
		final Publisher<RpcRequest> publisher=ServiceLocator.loadService(Publisher.class, "request");
		final CountDownLatch latch=new CountDownLatch(1000);
		Runnable task=new Runnable() {
			
			public void run() {
				publisher.publish(new Channel() {
					
					public int compareTo(Channel o) {
						return 0;
					}
					
					public <T> Attribute<T> attr(AttributeKey<T> key) {
						return null;
					}
					
					public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
						return null;
					}
					
					public ChannelFuture writeAndFlush(Object msg) {
						return null;
					}
					
					public ChannelFuture write(Object msg, ChannelPromise promise) {
						return null;
					}
					public ChannelFuture write(Object msg) {
						latch.countDown();
						return null;
					}
					public ChannelPromise voidPromise() {
						return null;
					}
					public Unsafe unsafe() {
						return null;
					}
					public SocketAddress remoteAddress() {
						return null;
					}
					public Channel read() {
						return null;
					}
					public ChannelPipeline pipeline() {
						return null;
					}
					public Channel parent() {
						return null;
					}
					public ChannelFuture newSucceededFuture() {
						return null;
					}
					public ChannelPromise newPromise() {
						return null;
					}
					public ChannelProgressivePromise newProgressivePromise() {
						return null;
					}
					public ChannelFuture newFailedFuture(Throwable cause) {
						return null;
					}
					public ChannelMetadata metadata() {
						return null;
					}
					public SocketAddress localAddress() {
						return null;
					}
					public boolean isWritable() {
						return false;
					}
					public boolean isRegistered() {
						return false;
					}
					public boolean isOpen() {
						return false;
					}
					public boolean isActive() {
						return false;
					}
					public ChannelId id() {
						return null;
					}
					public Channel flush() {
						return null;
					}
					
					public EventLoop eventLoop() {
						return null;
					}
					
					public ChannelFuture disconnect(ChannelPromise promise) {
						return null;
					}
					
					public ChannelFuture disconnect() {
						return null;
					}
					
					public ChannelFuture connect(SocketAddress remoteAddress,
							SocketAddress localAddress, ChannelPromise promise) {
						return null;
					}
					
					public ChannelFuture connect(SocketAddress remoteAddress,
							ChannelPromise promise) {
						return null;
					}
					
					public ChannelFuture connect(SocketAddress remoteAddress,
							SocketAddress localAddress) {
						return null;
					}
					
					public ChannelFuture connect(SocketAddress remoteAddress) {
						return null;
					}
					
					public ChannelConfig config() {
						return null;
					}
					
					public ChannelFuture closeFuture() {
						return null;
					}
					
					public ChannelFuture close(ChannelPromise promise) {
						return null;
					}
					
					public ChannelFuture close() {
						return null;
					}
					
					public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
						return null;
					}
					
					public ChannelFuture bind(SocketAddress localAddress) {
						return null;
					}
					
					public ByteBufAllocator alloc() {
						return null;
					}
				}, request);
				
			}
		};
		
		ExecutorService service=Executors.newFixedThreadPool(16);
		for(int i=0;i<1000;i++){
			service.submit(task);
		}
		latch.await();
	}
	
	public void testTwo() throws IOException, InterruptedException{
		testPublisher();
	}
}
