package com.ben.mc.AnthingTest.code_test;

import com.ben.mc.AnthingTest.mc.scan.IocTest1;
import com.ben.mc.AnthingTest.mc.xml.XmlIocTest1;
import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.application.DefaultApplicationContext;

public class Xml_Classprocessing {
	public static void main(String[] args) throws Throwable {
		new DefaultApplicationContext("bean.xml");

		IocTest1 i = BeanFactory.getBean("IocTest1");
		i.aaxx("xxxx");

		XmlIocTest1 x = BeanFactory.getBean("XmlIocTest1");
		x.aaxx("123xxxx");
		x.aaxx3("3", "123");
	}
}
