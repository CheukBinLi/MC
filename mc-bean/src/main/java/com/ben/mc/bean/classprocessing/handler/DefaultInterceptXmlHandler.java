package com.ben.mc.bean.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ben.mc.bean.classprocessing.ClassInfo;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/***
 * 
 * Copyright 2015 ZHOU.BING.LI Individual All
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
public class DefaultInterceptXmlHandler extends AbstractClassProcessingHandler<CtClass, Object> {

	public Set<Integer> thisType() {
		return new HashSet<Integer>(Arrays.asList(Type, Method));
	}

	public Class<Object> handlerClass() {
		return Object.class;
	}

	public HandlerInfo doProcessing(Map<String, Map> cache, CtClass newClazz, CtMember additional, Object config) throws Throwable {
		if (!(additional instanceof CtMethod))
			return null;
		com.ben.mc.bean.xml.DefaultConfigInfo.Intercept intercept = (com.ben.mc.bean.xml.DefaultConfigInfo.Intercept) config;
		CtMethod ctMethod = CtNewMethod.copy((CtMethod) additional, newClazz, null);
		boolean isReturn = !ctMethod.getReturnType().getName().equals("void");
		String imp = String.format("private com.ben.mc.bean.classprocessing.handler.Interception interception = new %s();", intercept.getRef());
		try {
			if (null == newClazz.getDeclaredField("interception"))
				newClazz.addField(CtField.make(imp, newClazz));
		} catch (NotFoundException e) {
			newClazz.addField(CtField.make(imp, newClazz));
		}

		StringBuffer sb = new StringBuffer("{");
		if (isReturn)
			sb.append("Object o;");
		sb.append("this.interception.before();");
		sb.append("if(this.interception.Intercept(this,");
		sb.append("BeanFactory.getClassInfoMethod(\"").append(newClazz.getName()).append("\",\"").append(ClassInfo.getMethod(ctMethod)).append("\")");
		sb.append(",$args)){");
		if (isReturn)
			sb.append("o= super.").append(ctMethod.getName()).append("($$);");
		else
			sb.append("super.").append(ctMethod.getName()).append("($$);");
		sb.append("}");
		if (isReturn)
			sb.append("else { o=" + ClassInfo.getReturn(ctMethod.getReturnType()) + ";}");
		sb.append("this.interception.after();");
		if (isReturn)
			sb.append("return o;");
		sb.append("}");
		ctMethod.setBody(sb.toString());
		newClazz.addMethod(ctMethod);
		return new HandlerInfo(null, newClazz, ctMethod, null);
	}
}
