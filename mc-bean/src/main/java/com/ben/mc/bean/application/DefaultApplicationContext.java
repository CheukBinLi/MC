package com.ben.mc.bean.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.ben.mc.bean.classprocessing.AbstractClassProcessingFactory;
import com.ben.mc.bean.classprocessing.CreateClassFactory;
import com.ben.mc.bean.classprocessing.CreateClassInfo;
import com.ben.mc.bean.classprocessing.DefaultClassProcessingFactory;
import com.ben.mc.bean.classprocessing.DefaultClassProcessingXmlFactory;
import com.ben.mc.bean.scan.Scan;
import com.ben.mc.bean.xml.DefaultConfigInfo;
import com.ben.mc.bean.xml.XmlHandler;

import javassist.ClassClassPath;
import javassist.ClassPool;

public class DefaultApplicationContext extends BeanFactory implements ApplicationContext {

	private static boolean isRuned = false;

	public <T> T getBeans(String name) throws Throwable {
		return getBean(name);
	}

	/***
	 * 
	 * @param Scan 搜索路径
	 * @param forced 是否强制加载
	 * @param initSystemClassLoader 是否附加到系统加载器
	 * @throws Throwable
	 */
	public DefaultApplicationContext(String Scan, boolean forced, boolean initSystemClassLoader) throws Throwable {
		this.action(Scan, false, forced, initSystemClassLoader);
	}

	/***
	 * 
	 * @param config xml文件名/XML文件路径
	 * @throws Throwable
	 */
	public DefaultApplicationContext(String config) throws Throwable {
		this(config, false);
	}

	/***
	 * 
	 * @param config xml文件名/XML文件路径
	 * @param forced 是否强制加载
	 * @throws Throwable
	 */
	public DefaultApplicationContext(String config, boolean forced) throws Throwable {
		super();
		this.action(config, true, forced, false);
	}

	public DefaultApplicationContext() {
		super();
	}

	private void action(String config, boolean isXml, boolean forced, boolean initSystemClassLoader) throws Throwable {
		if (isRuned && !forced)
			return;
		isRuned = true;
		DefaultConfigInfo configInfo = null;
		if (isXml) {
			//分析
			InputStream in;
			if (null == (in = Thread.currentThread().getContextClassLoader().getResourceAsStream(config))) {
				File file = new File(config);
				if (!file.exists())
					throw new Exception(String.format("没找到指定文件(%s),请检查路径!", config));
				in = new FileInputStream(file);
			}
			configInfo = (DefaultConfigInfo) XmlHandler.newInstance().read(in);
		}
		else
			configInfo = new DefaultConfigInfo(config, initSystemClassLoader);

		if (configInfo.isInitSystemClassLoader())
			ClassPool.getDefault().insertClassPath(new ClassClassPath(this.getClass()));
		AbstractClassProcessingFactory<CreateClassInfo> xmlX = new DefaultClassProcessingXmlFactory();
		AbstractClassProcessingFactory<CreateClassInfo> scanToPack = new DefaultClassProcessingFactory();
		CreateClassInfo beanQueue = xmlX.getCompleteClass(null, configInfo);
		if (null != configInfo.getScanToPack()) {
			beanQueue.addAll(scanToPack.getCompleteClass(Scan.doScan(configInfo.getScanToPack()), configInfo));
		}
		CreateClassFactory.newInstance().create(beanQueue, configInfo.isInitSystemClassLoader());
	}
}
