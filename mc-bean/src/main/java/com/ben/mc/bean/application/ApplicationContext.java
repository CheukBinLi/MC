package com.ben.mc.bean.application;

public interface ApplicationContext {

	<T> T getBeans(String name) throws Throwable;

}
