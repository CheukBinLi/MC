package com.ben.mc.AnthingTest.code_test;

import com.ben.mc.AnthingTest.IocTest1;
import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.application.DefaultApplicationContext;

public class Xml_Classprocessing {
	public static void main(String[] args) throws Throwable {
		new DefaultApplicationContext("bean.xml");

		IocTest1 i = BeanFactory.getBean("iocTest1");
		i.aaxx("xxxx");

	}
}
