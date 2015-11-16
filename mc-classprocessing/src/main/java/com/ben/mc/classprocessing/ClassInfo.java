package com.ben.mc.classprocessing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

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

}
