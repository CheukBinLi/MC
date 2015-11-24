package com.ben.mc.bean.classprocessing.handler;

import java.lang.reflect.Method;

public class DefaultInterception implements Interception {

	public boolean Intercept(Object arg, Method method, Object... params) {
		System.err.println("Intercept method:" + method.getName() + " params:" + params);
		return true;
	}

	public void before() {
		System.err.println("before");

	}

	public void after() {

		System.err.println("after");
	}

}
