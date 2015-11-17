package com.ben.mc.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.CtClass;
import javassist.CtMember;

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

	private Set<String> InterceptMethods = null;

	@Override
	public Class<Intercept> handlerClass() {
		return Intercept.class;
	}

	@Override
	public Intercept getCheck(CtMember clazz) throws ClassNotFoundException {
		Intercept i = super.getCheck(clazz);
		if (null != i) {
			if (i.method().length > 0)
				this.InterceptMethods = new HashSet<String>(Arrays.asList(i.method()));
		}
		return i;
	}

	@Override
	public HandlerInfo doProcessing(Map<String, Map> cache, CtClass t, CtMember additional) throws Throwable {
		System.err.println(this.getClass().getName() + ":" + additional.getName());
		if (null != this.InterceptMethods && this.InterceptMethods.contains(additional.getName())) {

		}
		//		HandlerInfo handlerInfo=new HandlerInfo(x, additional, imports);
		return null;
	}

	@Override
	public com.ben.mc.classprocessing.handler.ClassProcessingHandler.scope getScope() {
		return scope.TypeOrMetho;
	}

}
