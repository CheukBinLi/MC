package com.ben.mc.AnthingTest.code_test;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import javassist.CtClass;

import com.ben.mc.AnthingTest.IocTest1;
import com.ben.mc.classprocessing.BeanFactory;
import com.ben.mc.classprocessing.ClassProcessingFactory;
import com.ben.mc.classprocessing.DefaultClassProcessingFactory;
import com.ben.mc.scan.Scan;

public class Scan_Classprocessing {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, Throwable {
		//1.0
		//		old o = (old) ((ClazzInfo) new DefaultClassProcessing().classProcessing(old.class)).getClazz().newInstance();
		//		o.setFX("mba");
		//		System.out.println(o.getFX());
		//		o.x();
		//2.0

		ClassProcessingFactory<CtClass> classProcessingFactory = new DefaultClassProcessingFactory() {
		};

		Map<String, CtClass> result = classProcessingFactory.getCompleteClass(Scan.doScan("com.ben.mc.AnthingTest"),null);

		for (Entry<String, CtClass> en : result.entrySet())
			System.out.println(en.getValue().getName());

		//		Thread.sleep(50);
		//		AutoLoadTestI i = BeanFactory.getBean("autoLoadTestI");
		//		i.hello("王小牛");
		IocTest1 i = BeanFactory.getBean("iocTest1");
		i.aaxx("王小牛0_0");

	}

	static {

	}
}
