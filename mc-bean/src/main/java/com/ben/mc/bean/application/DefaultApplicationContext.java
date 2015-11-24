package com.ben.mc.bean.application;

public class DefaultApplicationContext extends BeanFactory implements ApplicationContext {

	public <T> T getBeans(String name) throws Throwable {
		return getBean(name);
	}

	public DefaultApplicationContext(String config) {
		super();
		//分析
	}

	public DefaultApplicationContext() {
		super();
	}

}
