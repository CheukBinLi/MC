package com.ben.mc.AnthingTest;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.annotation.Intercept;
import com.ben.mc.annotation.Register;

@Register
public class IocTest1 {

	private final static int a = 1;
	private String b = "b";
	private boolean c;

	@AutoLoad
	private AutoLoadTestI autoLoadTestImpl;

	@Intercept("aa")
	public void aaxx(String name) {
		autoLoadTestImpl.hello(name);
	}
}
