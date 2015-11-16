package com.ben.mc.classprocessing.handler;

import javassist.CtMember;

public abstract class AbstractClassProcessingHandler<O, A> implements ClassProcessingHandler<O, A, CtMember, HandlerInfo> {
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
