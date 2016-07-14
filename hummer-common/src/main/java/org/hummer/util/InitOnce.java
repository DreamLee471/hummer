package org.hummer.util;

import java.util.concurrent.Callable;

/**
 * 延迟加载工具类
 * @author liwei
 *
 * @param <T>
 */
public class InitOnce <T> {
	
	private T instance;
	
	private Callable<T> initFunc;
	
	public InitOnce(Callable<T> initFunc) {
		this.initFunc = initFunc;
	}

	public static <T> InitOnce<T> init(Callable<T> initFunc){
		return new InitOnce<T>(initFunc);
	}
	
	public T get(){
		if(instance == null){
			synchronized (this) {
				if(instance == null){
					try {
						instance = initFunc.call();
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		return instance;
	}

}
