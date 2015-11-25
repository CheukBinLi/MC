package com.ben.mc.bean.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ben.mc.bean.classprocessing.AbstractClassProcessingFactory;
import com.ben.mc.bean.classprocessing.ClassProcessingFactory;
import com.ben.mc.bean.classprocessing.DefaultClassProcessingFactory;
import com.ben.mc.bean.classprocessing.DefaultXmlClassProcessingFactory;
import com.ben.mc.bean.scan.Scan;
import com.ben.mc.bean.util.ShortNameUtil;
import com.ben.mc.bean.xml.DefaultConfigInfo;
import com.ben.mc.bean.xml.XmlHandler;
import com.ben.mc.cache.CachePoolFactory;
import com.ben.mc.cache.DefaultCachePoolFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

public class DefaultApplicationContext extends BeanFactory implements ApplicationContext {

	public <T> T getBeans(String name) throws Throwable {
		return getBean(name);
	}

	public DefaultApplicationContext(String config) throws Throwable {
		super();
		//分析
		InputStream in;
		if (null == (in = Thread.currentThread().getContextClassLoader().getResourceAsStream(config))) {
			File file = new File(config);
			if (!file.exists())
				throw new Exception(String.format("没找到指定文件(%s),请检查路径!", config));
			in = new FileInputStream(file);
		}
		DefaultConfigInfo configInfo = (DefaultConfigInfo) XmlHandler.newInstance().read(in);
		//步骤
		//		ClassPool cp = ClassPool.getDefault();
		//		CtClass clazz = cp.get("com.ben.mc.bean.application.BeanFactory");
		////		clazz.freeze();
		////		clazz.defrost();
		////		clazz.detach();
		//		clazz.setName(clazz.getName());
		//		CtField field = clazz.getDeclaredField("cachePoolFactory");
		//		clazz.removeField(field);
		//		CtField newfield = CtField.make("private static com.ben.mc.cache.CachePoolFactory cachePoolFactory = new com.ben.mc.cache.DefaultCachePoolFactory();", clazz);
		//		clazz.toClass();
		//		new BeanFactory().oo();
		//bean
		//intercept
		Map<String, CtClass> beans;
		AbstractClassProcessingFactory<List<Map<String, CtClass>>> xmlX = new DefaultXmlClassProcessingFactory();
		AbstractClassProcessingFactory<List<Map<String, CtClass>>> scanToPack = new DefaultClassProcessingFactory();
		//xml
		List<Map<String, CtClass>> xmlBeanQueue = xmlX.getCompleteClass(null, configInfo);
		//ScanToPack
		List<Map<String, CtClass>> scanToPackQueue = null;
		if (null != configInfo.getScanToPack()) {
			scanToPackQueue = scanToPack.getCompleteClass(Scan.doScan(configInfo.getScanToPack()), null);
		}
		//生成
		xmlX.anthingToClass(xmlBeanQueue);
		xmlX.anthingToClass(scanToPackQueue);
		//		for (int i = 0, len = xmlBeanQueue.size(); i < len; i++) {
		//			for (Entry<String, CtClass> en : xmlBeanQueue.get(i).entrySet()) {
		//				System.out.println(en.getValue().getName());
		//			}
		//		}
		//		System.out.println("------------");
		//		for (int i = 0, len = scanToPackQueue.size(); i < len; i++) {
		//			for (Entry<String, CtClass> en : scanToPackQueue.get(i).entrySet()) {
		//				System.out.println(en.getValue().getName());
		//			}
		//		}
	}

	public DefaultApplicationContext() {
		super();
	}

	public static void main(String[] args) {
		try {
			new DefaultApplicationContext("bean.xml");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
