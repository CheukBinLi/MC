package com.ben.mc.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import com.ben.mc.annotation.Intercept;

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

	public HandlerInfo doProcessing(Map<String, Map> cache, CtClass newClazz, CtMember additional) throws Throwable {
		System.err.println(this.getClass().getName() + ":" + additional.getName());
		//		HandlerInfo handlerInfo=new HandlerInfo(x, additional, imports);
		CtMethod ctMethod = CtNewMethod.copy((CtMethod) additional, newClazz, null);

		//		private com.ben.mc.classprocessing.handler.Interception interception = new com.ben.mc.classprocessing.handler.DefaultInterception();
		if (null == newClazz.getField("interception"))
			newClazz.addField(CtField.make("private com.ben.mc.classprocessing.handler.Interception interception = new com.ben.mc.classprocessing.handler.DefaultInterception();", newClazz));

		StringBuffer sb = new StringBuffer("{");
		sb.append("if(this.interception.Intercept(this,null,new Object[1])){");
		if (ctMethod.getReturnType().getName().equals("void"))
			sb.append("super.").append(ctMethod.getName()).append("($$);");
		else
			sb.append("return super.").append(ctMethod.getName()).append("($$);");
		sb.append("}");
		sb.append("}");
		System.err.println(sb.toString());
		ctMethod.setBody(sb.toString());
		//		ctMethod.setBody("{System.err.println(\"asdf\");}");
		//		ctMethod.setBody("{if(interception.Intercept(this,method,$$){})}");
		//

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

	//	static {
	//		{
	//			if (this.interception.Intercept(this, null, new Object[]{$$})) {
	//				super.aaxx($$);
	//			}
	//		}
	//	}
}
