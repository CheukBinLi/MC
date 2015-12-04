package com.ben.mc.AnthingTest.mc.scan;

import com.ben.mc.annotation.Register;

@Register
public class IocTest2 {

	private AutoLoadTestI a = new AutoLoadTestImpl();

	public void x() {
		a.hello("asdf");
	}

}
