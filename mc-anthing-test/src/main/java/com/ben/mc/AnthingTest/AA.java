package com.ben.mc.AnthingTest;


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
