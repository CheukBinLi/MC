package com.ben.mc.classprocessing.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.CtClass;

/***
 * 
 * Copyright 2015    ZHOU.BING.LI Individual All
 *  
 * ALL RIGHT RESERVED
 *  
 * CREATE ON 2015年11月13日 上午10:26:33
 *  
 * EMAIL:20796698@QQ.COM
 *  
 * GITHUB:https://github.com/fdisk123
 * 
 * @author ZHUO.BIN.LI
 * 
 * @see
 * 
 * @param <O> 传入要解释的ctclazz
 * @param <A> annotation对象
 */
public interface ClassProcessingHandler<O, A, I, R> {

	public static enum scope {
		Type, Field, Method, TypeOrField, FieldOrMethod, TypeOrMetho, All
	};

	Class<A> handlerClass();

	/***
	 * 运行范围
	 * @return
	 */
	scope getScope();

	public A getCheck(I x) throws Throwable;

	/***
	 * 
	 * @param cache 内存对象
	 * 		final Map<String, CtClass> REGISTER_CACHE = new ConcurrentHashMap<String, CtClass>();
	 * 		final Map<String, CtClass> NICK_NAME_CACHE = new ConcurrentHashMap<String, CtClass>();
	 *		final Map<String, String>  AUTO_LOAD_CACHE = new ConcurrentHashMap<String, String>();
	 * @param O newClass
	 * @param additional 附加对像 ctfield/ctmethod...
	 * @return 装拼完成的对象
	 */
	public R doProcessing(Map<String, Map> cache, O t, I additional) throws Throwable;

}
