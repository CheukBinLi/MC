package com.ben.mc.AnthingTest.mc.xml;

import com.ben.mc.AnthingTest.mc.scan.AutoLoadTestI;
import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.annotation.Intercept;
import com.ben.mc.annotation.Register;

public class XmlIocTest1 {

	private final static int a = 1;
	private String b = "b";
	private boolean c;

	private AutoLoadTestI autoLoadTestImpl;

	public void aaxx(String name) {
		autoLoadTestImpl.hello(name);
	}

	public void aaxx2(String name, String sex) {
		autoLoadTestImpl.hello(name);
	}

	public String aaxx3(String name, String sex) {
		autoLoadTestImpl.hello(name);
		return "xx";
	}

}
