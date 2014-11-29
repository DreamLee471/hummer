package org.hummer.common.test;

import java.lang.reflect.Method;

import org.hummer.util.HummerUtils;

import junit.framework.TestCase;

public class HummerUtilsTest extends TestCase {
	
	public void testGetMethodDescriptor() throws SecurityException, NoSuchMethodException{
		Method m=String.class.getDeclaredMethod("toString", new Class[]{});
		String descriptor=HummerUtils.getMethodDescriptor(m);
		assertEquals("toString()Ljava/lang/String;", descriptor);
	}

}
