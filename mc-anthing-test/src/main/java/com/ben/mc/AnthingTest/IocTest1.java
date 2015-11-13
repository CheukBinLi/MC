package com.ben.mc.AnthingTest;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.annotation.Register;

@Register("IocTest1")
public class IocTest1 {

	private final static int a = 1;
	private String b = "b";
	private boolean c;

	//	@AutoLoad("AutoLoadTestImpl")
	@AutoLoad
	/* ("autoLoadTestI") */
	private AutoLoadTestI autoLoadTestImpl;
}
