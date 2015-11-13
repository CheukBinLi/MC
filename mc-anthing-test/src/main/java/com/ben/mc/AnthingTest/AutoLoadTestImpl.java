package com.ben.mc.AnthingTest;

import com.ben.mc.annotation.Register;

@Register("autoLoadTestI")
public class AutoLoadTestImpl implements AutoLoadTestI {

	public void hello(String name) {
		System.out.println(name + "你好!");
	}

}
