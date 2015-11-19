package com.ben.mc.classprocessing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/***
 * 
 * Copyright 2015    ZHOU.BING.LI Individual All
 *  
 * ALL RIGHT RESERVED
 *  
 * CREATE ON 2015年11月15日 下午1:18:29
 *  
 * EMAIL:20796698@QQ.COM
 *  
 * GITHUB:https://github.com/fdisk123
 * 
 * @author ZHUO.BIN.LI
 * 
 * @see 类属性
 *
 */
@SuppressWarnings("rawtypes")
public class ClassInfo {

	private String name;
	private Class clazz;
	private Map<String, Field> fields;
	private Map<String, Method> methods;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	public void setFields(Map<String, Field> fields) {
		this.fields = fields;
	}

	public Map<String, Method> getMethods() {
		return methods;
	}

	public void setMethods(Map<String, Method> methods) {
		this.methods = methods;
	}

	public ClassInfo() {
		super();
	}

	/***
	 * 
	 * @param name 名称
	 * @param clazz 扩展用：null
	 * @param fields  字段
	 * @param methods 方法
	 */
	public ClassInfo(String name, Class clazz, Map<String, Field> fields, Map<String, Method> methods) {
		super();
		this.name = name;
		this.clazz = clazz;
		this.fields = fields;
		this.methods = methods;
	}

	/***
	 * 方法名加工
	 * @param m
	 * @return
	 */
	public static final String getMethod(Method m) {
		String ax = String.format("%s %s %s(%s);", Modifier.toString(m.getModifiers()), m.getReturnType().getName(), m.getName(), getParameterTypes(m.getParameterTypes()));
		//		System.out.println(ax);
		return ax;
	}

	/***
	 * 方法名加工
	 * @param m
	 * @return
	 */
	public static final String getMethod(CtMethod m) throws NotFoundException {
		String ax = String.format("%s %s %s(%s);", Modifier.toString(m.getModifiers()), m.getReturnType().getName(), m.getName(), getParameterTypes(m.getParameterTypes()));
		//		System.out.println(ax);
		return ax;
	}

	protected static final ArrayList<String> getParameterTypes(CtClass... c) {
		ArrayList<String> a = new ArrayList<String>();
		for (CtClass b : c)
			a.add(b.getName());
		return a;
	}

	protected static final ArrayList<String> getParameterTypes(Class... c) {
		ArrayList<String> a = new ArrayList<String>();
		for (Class b : c)
			a.add(b.getName());
		return a;
	}

	/***
	 * 返回类型
	 * @param c
	 * @return
	 */
	public static final String getReturn(CtClass c) {
		if (c.isArray())
			return "null";
		else if (c.getSimpleName().equalsIgnoreCase("boolean"))
			return "false";
		else if (c.getSimpleName().equals("String"))
			return "null";
		else if (c.getSimpleName().equals("Integer"))
			return "null";
		else if (c.getSimpleName().equals("int"))
			return "-1";
		else if (c.getSimpleName().equals("byte"))
			return "-1";
		else if (c.getSimpleName().equals("Byte"))
			return "null";
		else if (c.getSimpleName().equals("char"))
			return "0";
		else if (c.getSimpleName().equals("Char"))
			return "null";
		else if (c.getSimpleName().equals("Double"))
			return "null";
		else if (c.getSimpleName().equals("double"))
			return "-1D";
		else if (c.getSimpleName().equals("long"))
			return "-1L";
		else if (c.getSimpleName().equalsIgnoreCase("Long"))
			return "null";
		else if (c.getSimpleName().equals("short"))
			return "-1";
		else if (c.getSimpleName().equals("Short"))
			return "null";
		else if (c.getSimpleName().equals("Float"))
			return "null";
		else if (c.getSimpleName().equals("float"))
			return "-1F";
		return "";
	}

	public static void main(String[] args) throws NotFoundException {
		Class c = DefaultMethodPool.class;

		CtClass ctC = ClassPool.getDefault().get(c.getName());

		Method[] methods = c.getDeclaredMethods();

		CtMethod[] ctMethods = ctC.getDeclaredMethods();

		for (Method m : methods)
			System.out.println(getMethod(m));
		for (CtMethod m : ctMethods) {
			System.err.println(getMethod(m) + " return " + m.getReturnType().getSimpleName());
			System.out.println(getReturn(m.getReturnType()));
		}
	}

}
