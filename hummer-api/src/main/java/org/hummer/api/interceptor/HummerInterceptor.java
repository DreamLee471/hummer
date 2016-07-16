package org.hummer.api.interceptor;

public interface HummerInterceptor <T> {
	
	public void handle(T t);

}
