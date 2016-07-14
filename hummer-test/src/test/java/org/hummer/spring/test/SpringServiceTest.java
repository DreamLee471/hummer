package org.hummer.spring.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServiceTest extends TestCase {
	
	public void testHummerSpring() throws IOException{
		ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("classpath:Hummer-Service.xml");
		context.start();
		System.in.read();
	}

}
