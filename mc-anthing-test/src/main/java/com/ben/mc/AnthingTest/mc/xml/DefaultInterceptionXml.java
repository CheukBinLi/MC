package com.ben.mc.AnthingTest.mc.xml;

import java.lang.reflect.Method;

import com.ben.mc.bean.classprocessing.handler.Interception;

public class DefaultInterceptionXml implements Interception {

	public boolean Intercept(Object arg, Method method, Object... params) {
		System.err.println("XML_Intercept method:" + method.getName() + " params:" + params);
		return true;
	}

	public void before() {
		System.err.println("before");

	}

	public void after() {

		System.err.println("after");
	}

}
