package com.ben.mc.bean.classprocessing.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.ben.mc.bean.xml.DefaultConfigInfo;

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
 * @param <I> Check CtField/CtMethod 注解
 * @param <II>getCheckII CtClass 获取TYPE注解
 * @param <R> doProcessing 返回
 */
public interface ClassProcessingHandler<O, A, I, II, R> {

	public static final int Type = 0x00000001;
	public static final int Field = 0x00000002;
	public static final int Method = 0x00000004;

	Class<A> handlerClass();

	/***
	 * 运行范围
	 * @return
	 */
	//	scope getScope();

	public A getCheck(I x) throws Throwable;

	public A getCheck(I x, int type) throws Throwable;

	public A getCheckII(II x) throws Throwable;

	public A getCheckII(II x, int type) throws Throwable;

	public Set<Integer> thisType();

	/***
	 * 
	 * @param cache 内存对象
	 * 		final Map<String, CtClass> REGISTER_CACHE = new ConcurrentHashMap<String, CtClass>();
	 * 		final Map<String, CtClass> NICK_NAME_CACHE = new ConcurrentHashMap<String, CtClass>();
	 *		final Map<String, String>  AUTO_LOAD_CACHE = new ConcurrentHashMap<String, String>();
	 *      xml的情况,ML配置 
	 *      final Map<String, DefaultConfigInfo> XML_CONFIG_CACHE=new hashmap<String, DefaultConfigInfo>();
	 *      --[XML_CONFIG_CACHE[XML_CONFIG_CACHE]]。
	 *      ---XML_CONFIG_CACHE.put(XML_CONFIG_CACHE,
	 *      ---final DefaultConfigInfo XML_CONFIG_CACHE = (DefaultConfigInfo) config);
	 * @param O newClass
	 * @param additional 附加对像 ctfield/ctmethod...
	 * @return 装拼完成的对象
	 */
	public R doProcessing(Map<String, Map> cache, O newClazz, I additional, Object config) throws Throwable;

}
