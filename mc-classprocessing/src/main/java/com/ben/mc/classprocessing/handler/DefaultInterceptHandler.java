package com.ben.mc.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.CtClass;
import javassist.CtMember;
import javassist.CtMethod;

import com.ben.mc.annotation.Intercept;

/***
 * 
 * Copyright 2015    ZHOU.BING.LI Individual All
 *  
 * ALL RIGHT RESERVED
 *  
 * CREATE ON 2015年11月17日 下午2:37:08
 *  
 * EMAIL:20796698@QQ.COM
 *  
 * GITHUB:https://github.com/fdisk123
 * 
 * @author ZHUO.BIN.LI
 * 
 * @see 方法拦截器处理器
 *
 */
@SuppressWarnings("rawtypes")
public class DefaultInterceptHandler extends AbstractClassProcessingHandler<CtClass, Intercept> {

	@Override
	public Set<Integer> thisType() {
		return new HashSet<Integer>(Arrays.asList(Type, Method));
	}

	@Override
	public Class<Intercept> handlerClass() {
		return Intercept.class;
	}

	@Override
	public HandlerInfo doProcessing(Map<String, Map> cache, CtClass t, CtMember additional) throws Throwable {
		System.err.println(this.getClass().getName() + ":" + additional.getName());
		//		HandlerInfo handlerInfo=new HandlerInfo(x, additional, imports);
		CtMethod ctMethod = (CtMethod) additional;

		com.ben.mc.classprocessing.handler.Interception interception = null;

		ctMethod.setBody("");
		//

		return null;
	}

}
