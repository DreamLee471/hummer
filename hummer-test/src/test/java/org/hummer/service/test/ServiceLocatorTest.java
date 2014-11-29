package org.hummer.service.test;

import junit.framework.TestCase;

import org.hummer.api.protocol.HummerProtocol;
import org.hummer.service.ServiceLocator;

public class ServiceLocatorTest extends TestCase {
	
	public void testLoadService(){
		HummerProtocol protocol=ServiceLocator.loadService(HummerProtocol.class, "hummer1.1");
		assertNotNull(protocol);
	}

}
