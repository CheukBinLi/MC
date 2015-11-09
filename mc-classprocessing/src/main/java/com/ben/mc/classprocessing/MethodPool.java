package com.ben.mc.classprocessing;

import java.lang.reflect.Method;
import java.util.Map;

public interface MethodPool {

	public Method getMethod(Class clazz, String methodName);

	public Map<String, Method> getMethods(Class clazz);

	public void put(Class clazz);

	public void put(Class clazz, Method method);

	public void remove(Class clazz);

	public void remove(Class clazz, Method method);

	public void containsKey(Class clazz);

	public void containsKey(Class clazz, Method method);

}
