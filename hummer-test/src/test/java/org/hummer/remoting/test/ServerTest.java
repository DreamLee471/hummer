package org.hummer.remoting.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.hummer.client.conf.ClientMetaData;
import org.hummer.client.proxy.ProxyFactory;
import org.hummer.service.test.IHello;

import junit.framework.TestCase;

public class ServerTest extends TestCase {

	private static String buffer;
	static{
		StringBuilder bufferS=new StringBuilder(1024);
		for(int i=0;i<1024;i++){
			bufferS.append((char)('A'+(int)(Math.random()*26)));
		}
		buffer=bufferS.toString();
	}

	public void testServerService() throws Exception {
		long start=System.currentTimeMillis();
		ClientMetaData metadata=new ClientMetaData();
		metadata.setService(IHello.class.getName());
		metadata.setVersion("1.0.0");
		final IHello helloTarget=(IHello)ProxyFactory.getProxy(metadata);
		ExecutorService service = Executors.newFixedThreadPool(1);
		final CountDownLatch latch=new CountDownLatch(100000);
		final AtomicInteger timeout=new AtomicInteger();
		for(int i=0;i<100000;i++){
			service.submit(new Runnable() {
				
				public void run() {
					try{
						helloTarget.sayHello(buffer);
					}catch(Exception e){
						timeout.incrementAndGet();
						e.printStackTrace();
					}
					latch.countDown();
				}
			});
		}
		latch.await();
		System.out.println("cost:"+(System.currentTimeMillis()-start)/100000.0);
		System.out.println("timeout:"+timeout.get());
	}
}
