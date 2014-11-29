package org.hummer.spring.test;

import java.io.IOException;

import org.hummer.service.test.IHello;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class SpringServiceTest extends TestCase {
	
	public void testHummerSpring() throws IOException{
		ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("classpath:Hummer-Service.xml");
		IHello hello=(IHello)context.getBean("test");
		System.out.println(hello.sayHello("test1111"));
	}

}
