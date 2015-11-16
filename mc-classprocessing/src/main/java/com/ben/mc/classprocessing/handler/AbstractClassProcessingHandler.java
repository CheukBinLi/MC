package com.ben.mc.classprocessing.handler;

import javassist.CtMember;

public abstract class AbstractClassProcessingHandler<O, A, R> implements ClassProcessingHandler<O, A, CtMember, R> {
	protected A a;

	@SuppressWarnings("unchecked")
	@Override
	public A getCheck(CtMember clazz) throws ClassNotFoundException {
		Object o = clazz.getAnnotation(handlerClass());
		if (null != o) {
			this.a = (A) o;
			return this.a;
		}
		return null;
	}

}
