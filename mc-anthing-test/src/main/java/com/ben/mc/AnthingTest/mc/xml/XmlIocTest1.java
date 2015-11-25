package com.ben.mc.AnthingTest.mc.xml;

public class XmlIocTest1 {

	private final static int a = 1;
	private String b = "b";
	private boolean c;

	private XmlAutoLoadTestI autoLoadTestImpl;

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
