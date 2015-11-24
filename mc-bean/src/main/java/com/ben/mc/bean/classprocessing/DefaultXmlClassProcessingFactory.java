package com.ben.mc.bean.classprocessing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.ben.mc.bean.application.DefaultApplicationContext;
import com.ben.mc.bean.classprocessing.handler.ClassProcessingHandler;
import com.ben.mc.bean.classprocessing.handler.DefaultXmlAutoLoadHandler;
import com.ben.mc.bean.classprocessing.handler.HandlerInfo;
import com.ben.mc.bean.util.ShortNameUtil;
import com.ben.mc.bean.xml.DefaultConfigInfo;
import com.ben.mc.bean.xml.DefaultConfigInfo.Bean;
import com.ben.mc.cache.DefaultCachePoolFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

@SuppressWarnings({ "rawtypes", "unused" })
public abstract class DefaultXmlClassProcessingFactory extends AbstractClassProcessingFactory<CtClass> {

	public Map<String, CtClass> getCompleteClass(Set<String> clazzs, Object config) throws Throwable {
		final ConcurrentHashMap<String, CtClass> complete = new ConcurrentHashMap<String, CtClass>();
		final ConcurrentHashMap<String, String> nick = new ConcurrentHashMap<String, String>();
		final ConcurrentHashMap<String, String> shortName = new ConcurrentHashMap<String, String>();

		//		final CountDownLatch countDownLatch = new CountDownLatch(clazzs.size());

		final DefaultConfigInfo configInfo = (DefaultConfigInfo) config;

		Map<String, Map> cache = new HashMap<String, Map>();
		Map<String, DefaultConfigInfo> configMap = new HashMap<String, DefaultConfigInfo>();
		configMap.put(ClassProcessingFactory.XML_CONFIG_CACHE, configInfo);
		cache.put(ClassProcessingFactory.REGISTER_CACHE, complete);
		cache.put(ClassProcessingFactory.NICK_NAME_CACHE, nick);
		cache.put(ClassProcessingFactory.SHORT_NAME_CACHE, shortName);
		cache.put(ClassProcessingFactory.XML_CONFIG_CACHE, configMap);

		configInfo.getBeans();
		Iterator<Entry<String, Bean>> it = configInfo.getBeans().entrySet().iterator();
		//添加注册
		Entry<String, Bean> tempEn;
		ClassPool pool = ClassPool.getDefault();
		CtClass tempClazz;
		//		Map<String, LinkedList<Bean>> beans = new HashMap<String, LinkedList<Bean>>();//任务列表
		//		Bean tempBean;
		//		for (Entry<String, Bean> en : configInfo.getBeans().entrySet()) {
		//			tempBean = en.getValue();
		//			if (beans.containsKey(tempBean.getClassName())) {
		//				beans.get(tempBean.getClassName()).add(tempBean);
		//			}
		//			else
		//				beans.put(tempBean.getClassName(), new ArrayList<DefaultConfigInfo.Bean>());

		//		}
		//		System.err.println(beans);
		//		for (Bean b : beans.get("x"))
		//			System.err.println(b.getClassName() + ":" + b.getName());
		final String beans = "beans";
		while (it.hasNext()) {
			tempEn = it.next();
			System.out.println(tempEn.getValue().getClassName());
			tempClazz = pool.get(tempEn.getValue().getClassName());
			complete.put(tempEn.getValue().getClassName(), tempClazz);
			nick.put(tempEn.getKey(), tempEn.getValue().getName());
			shortName.put(ShortNameUtil.makeShortName(tempEn.getValue().getClassName()), tempEn.getValue().getClassName());

			//任务列表分组
			//			if (beans.containsKey(tempEn.getValue().getClassName())) {
			//				beans.get(tempEn.getValue().getClassName()).add(tempEn.getValue());
			//			}
			//			else
			//				beans.put(tempEn.getValue().getClassName(), new LinkedList<DefaultConfigInfo.Bean>());
			DefaultCachePoolFactory.newInstance().addNFloop4Map(false, tempEn.getValue(), beans, tempEn.getValue().getClassName(), tempEn.getValue().getType(), tempEn.getKey());

		}

		//		for (Entry<String, CtClass> en : complete.entrySet())
		//			System.err.println(en.getKey());

		DefaultXmlAutoLoadHandler autoLoadHandler = new DefaultXmlAutoLoadHandler();

		Iterator<Entry<String, CtClass>> ctEn = complete.entrySet().iterator();
		Entry<String, CtClass> tempCtEn;
		CtClass superClass;
		CtClass newClass;
		CtField[] ctFields;
		CtMethod[] ctMethods;
		HandlerInfo handlerInfo;
		while (ctEn.hasNext()) {
			tempCtEn = ctEn.next();
			superClass = tempCtEn.getValue();
			newClass = superClass.getClassPool().makeClass(superClass.getName() + Impl);
			newClass.setSuperclass(superClass);
			ctFields = superClass.getDeclaredFields();
			ctMethods = superClass.getDeclaredMethods();
			//			tempBean = configInfo.getBeans().get(key);
			Map<String, Bean> tempB = DefaultCachePoolFactory.newInstance().get4Map(beans, superClass.getName(), "field");

			for (CtField f : ctFields)
				for (Entry<String, Bean> en : tempB.entrySet()) {
					if (f.getName().equals(en.getKey()))
						handlerInfo = autoLoadHandler.doProcessing(cache, newClass, f, en.getValue());
				}
			System.err.println(tempB);

		}

		//		autoLoadHandler.doProcessing(cache, newClass, additional)

		return null;
	}
}
