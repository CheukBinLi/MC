package com.ben.mc.AnthingTest.code_test;

import com.ben.mc.AnthingTest.mc.scan.IocTest1;
import com.ben.mc.AnthingTest.mc.xml.XmlIocTest1;
import com.ben.mc.bean.application.ApplicationContext;
import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.application.DefaultApplicationContext;

public class Xml_Classprocessing {
	public static void main(String[] args) throws Throwable {
		ApplicationContext ac = new DefaultApplicationContext("bean.xml");

		IocTest1 i = BeanFactory.getBean("IocTest1");
		i.aaxx("xxxx");
		i.aaxx2("asdfasdfasfasfsadfsafsdf", null);

		XmlIocTest1 x = BeanFactory.getBean("XmlIocTest1");
		//		x.aaxx("123xxxx");
		//		x.aaxx2("3", "123");
		x.aaxx3("3", "123");
	}
}
