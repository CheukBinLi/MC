package com.ben.mc.AnthingTest;

import com.ben.mc.AnthingTest.mc.scan.AutoLoadTestImpl;

public class AA extends A {

	public AA() {
		super();
		autoLoadTestI = new AutoLoadTestImpl();
	}

	public void a() {
		super.a();
	}

	public static void main(String[] args) {
		new AA().a();
	}
}
