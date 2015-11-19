package com.ben.mc.classprocessing.handler;

import java.lang.reflect.Method;

public class DefaultInterception implements Interception {

	@Override
	public boolean Intercept(Object arg, Method method, Object... params) {
		System.err.println("Intercept method:"+method.getName()+" params:"+params);
		return true;
	}

	@Override
	public void before() {
		System.err.println("before");

	}

	@Override
	public void after() {

		System.err.println("after");
	}

}
