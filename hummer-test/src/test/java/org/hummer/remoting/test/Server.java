package org.hummer.remoting.test;

import java.io.IOException;

import org.hummer.api.service.ServiceMetadata;
import org.hummer.service.center.register.ServiceRegistry;
import org.hummer.service.test.HelloService;
import org.hummer.service.test.IHello;

public class Server {

	public static void main(String[] args) throws IOException {

		new Thread(new Runnable() {

			public void run() {				IHello hello = new HelloService();				ServiceMetadata metadata = new ServiceMetadata();				metadata.setServiceName(IHello.class.getName());				metadata.setTarget(hello);				metadata.setVersion("1.0.0");				ServiceRegistry.registerService(metadata);
			}
		}).start();

	}

}
