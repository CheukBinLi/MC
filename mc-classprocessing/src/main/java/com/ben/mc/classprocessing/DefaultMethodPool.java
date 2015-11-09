package com.ben.mc.classprocessing;

import java.lang.annotation.Retention;
import java.lang.reflect.Method;
import java.util.Map;

import org.junit.After;

public class DefaultMethodPool implements MethodPool {

	private static final DefaultMethodPool newInstance = new DefaultMethodPool();

	public static final DefaultMethodPool newInstance() {
		return newInstance;
	}

	private DefaultMethodPool() {
		super();
	}

	private Map<String, Object> getSource() {
		//		Cache
		return null;
	}

	public Method getMethod(Class clazz, String methodName) {
		return null;
	}

	public Map<String, Method> getMethods(Class clazz) {
		return null;
	}

	public void put(Class clazz) {

	}

	public void put(Class clazz, Method method) {

	}

	public void remove(Class clazz) {

	}

	public void remove(Class clazz, Method method) {

	}

	public void containsKey(Class clazz) {

	}

	public void containsKey(Class clazz, Method method) {

	}

}
