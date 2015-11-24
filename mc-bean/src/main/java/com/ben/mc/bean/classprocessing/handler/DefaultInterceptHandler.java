package com.ben.mc.bean.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ben.mc.annotation.Intercept;
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
public class DefaultInterceptHandler extends AbstractClassProcessingHandler<CtClass, Intercept> {

	public Set<Integer> thisType() {
		return new HashSet<Integer>(Arrays.asList(Type, Method));
	}

	public Class<Intercept> handlerClass() {
		return Intercept.class;
	}

	public HandlerInfo doProcessing(Map<String, Map> cache, CtClass newClazz, CtMember additional, Object config) throws Throwable {
		if (!(additional instanceof CtMethod))
			return null;
		CtMethod ctMethod = CtNewMethod.copy((CtMethod) additional, newClazz, null);
		boolean isReturn = !ctMethod.getReturnType().getName().equals("void");
		//		private com.ben.mc.classprocessing.handler.Interception interception = new com.ben.mc.classprocessing.handler.DefaultInterception();
		String imp = String.format("private com.ben.mc.bean.classprocessing.handler.Interception interception = new %s();", this.a.value());
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
		//		System.err.println(sb.toString());
		ctMethod.setBody(sb.toString());
		newClazz.addMethod(ctMethod);
		return new HandlerInfo(null, newClazz, ctMethod, null);
	}

	public static void main(String[] args) throws NotFoundException {
		ClassPool pool = ClassPool.getDefault();
		CtClass c = pool.get("com.ben.mc.classprocessing.handler.DefaultInterception");
		CtMethod[] ms = c.getDeclaredMethods();
		for (CtMethod m : ms) {
			Object o = m.getReturnType();
			//			if(m.getReturnType().getName().equals("void"))		
		}
		Object o = new Object[] {};
	}
}
