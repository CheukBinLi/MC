package com.ben.mc.classprocessing.handler;

import javassist.CtMember;

public abstract class AbstractClassProcessingHandler<O, A> implements ClassProcessingHandler<O, A, CtMember, HandlerInfo> {
	protected A a;

	@Override
	public A getCheck(CtMember x, int type) throws Throwable {
		if (this.thisType().contains(type))
			return getCheck(x);
		return null;
	}

	@SuppressWarnings("unchecked")
	public A getCheck(CtMember clazz) throws ClassNotFoundException {
		Object o = clazz.getAnnotation(handlerClass());
		if (null != o) {
			this.a = (A) o;
			return this.a;
		}
		return null;
	}
}
