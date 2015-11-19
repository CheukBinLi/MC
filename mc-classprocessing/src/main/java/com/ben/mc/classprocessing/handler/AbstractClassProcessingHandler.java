package com.ben.mc.classprocessing.handler;

import javassist.CtClass;
import javassist.CtMember;

@SuppressWarnings("unchecked")
public abstract class AbstractClassProcessingHandler<O, A> implements ClassProcessingHandler<O, A, CtMember, CtClass, HandlerInfo> {
	protected A a;

	public A getCheck(CtMember x, int type) throws Throwable {
		if (this.thisType().contains(type))
			return getCheck(x);
		return null;
	}

	public A getCheck(CtMember clazz) throws ClassNotFoundException {
		Object o = clazz.getAnnotation(handlerClass());
		if (null != o) {
			this.a = (A) o;
			return this.a;
		}
		return null;
	}

	@Override
	public A getCheckII(CtClass x) throws Throwable {
		Object o = x.getAnnotation(handlerClass());
		if (null != o) {
			this.a = (A) o;
			return this.a;
		}
		return null;
	}

	@Override
	public A getCheckII(CtClass x, int type) throws Throwable {
		if (this.thisType().contains(type))
			return getCheckII(x);
		return null;
	}

}
