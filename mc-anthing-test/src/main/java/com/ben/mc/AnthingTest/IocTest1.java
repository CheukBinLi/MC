package com.ben.mc.AnthingTest;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.annotation.Intercept;
import com.ben.mc.annotation.Register;

@Register
@Intercept("com.ben.mc.classprocessing.handler.DefaultInterception")
public class IocTest1 {

	private final static int a = 1;
	private String b = "b";
	private boolean c;

	@AutoLoad
	private AutoLoadTestI autoLoadTestImpl;

	//@Intercept("com.ben.mc.classprocessing.handler.DefaultInterception")
	public void aaxx(String name) {
		autoLoadTestImpl.hello(name);
	}

	//@Intercept("com.ben.mc.classprocessing.handler.DefaultInterception")
	public void aaxx2(String name, String sex) {
		autoLoadTestImpl.hello(name);
	}

	//@Intercept("com.ben.mc.classprocessing.handler.DefaultInterception")
	public String aaxx3(String name, String sex) {
		autoLoadTestImpl.hello(name);
		return "xx";
	}

}
