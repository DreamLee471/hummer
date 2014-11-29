package org.hummer.config;

import java.util.concurrent.TimeUnit;

import org.hummer.service.ServiceLocator;

import junit.framework.TestCase;

public class ConfigTest extends TestCase {
	
	public void testConfigInit() throws InterruptedException{
//		ConfigClient.register("test", "1.0.0");
		TimeUnit.SECONDS.sleep(20);
	}
	
	public void testRegister() throws InterruptedException{
		final ConfigCommetService configService=ServiceLocator.loadService(ConfigCommetService.class);
		configService.watch("org.hummer.config.ConfigCommetService", "1.0.0", new ConfigWatcher() {
			
			public void onEvent(WatchEvent evt) {
				System.out.println(configService.getChildConfig("org.hummer.config.ConfigCommetService", "1.0.0"));
			}
		});
		configService.subscribe("org.hummer.config.ConfigCommetService", "1.0.0");
		TimeUnit.SECONDS.sleep(20);
	}

}
