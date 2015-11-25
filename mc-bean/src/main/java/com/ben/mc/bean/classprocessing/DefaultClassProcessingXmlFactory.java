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

import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.application.DefaultApplicationContext;
import com.ben.mc.bean.classprocessing.handler.ClassProcessingHandler;
import com.ben.mc.bean.classprocessing.handler.DefaultAutoLoadXmlHandler;
import com.ben.mc.bean.classprocessing.handler.DefaultInterceptXmlHandler;
import com.ben.mc.bean.classprocessing.handler.HandlerInfo;
import com.ben.mc.bean.util.ShortNameUtil;
import com.ben.mc.bean.xml.DefaultConfigInfo;
import com.ben.mc.bean.xml.XmlType;
import com.ben.mc.bean.xml.DefaultConfigInfo.Bean;
import com.ben.mc.bean.xml.DefaultConfigInfo.Intercept;
import com.ben.mc.cache.DefaultCachePoolFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;

@SuppressWarnings({ "rawtypes", "unused" })
public class DefaultClassProcessingXmlFactory extends AbstractClassProcessingFactory<List<Map<String, CtClass>>> {

	public List<Map<String, CtClass>> getCompleteClass(Set<String> clazzs, Object config) throws Throwable {
		final ConcurrentHashMap<String, CtClass> complete = new ConcurrentHashMap<String, CtClass>();
		final ConcurrentHashMap<String, String> nick = new ConcurrentHashMap<String, String>();
		final ConcurrentHashMap<String, String> shortName = new ConcurrentHashMap<String, String>();

		final DefaultConfigInfo configInfo = (DefaultConfigInfo) config;

		Map<String, Map> cache = new HashMap<String, Map>();
		Map<String, DefaultConfigInfo> configMap = new HashMap<String, DefaultConfigInfo>();
		configMap.put(ClassProcessingFactory.XML_CONFIG_CACHE, configInfo);
		cache.put(ClassProcessingFactory.REGISTER_CACHE, complete);
		cache.put(ClassProcessingFactory.NICK_NAME_CACHE, nick);
		cache.put(ClassProcessingFactory.SHORT_NAME_CACHE, shortName);
		cache.put(ClassProcessingFactory.XML_CONFIG_CACHE, configMap);
		//分组
		List<Map<String, CtClass>> compileObject = new ArrayList<Map<String, CtClass>>();

		Map<String, CtClass> A1 = new HashMap<String, CtClass>();
		Map<String, CtClass> A2 = new HashMap<String, CtClass>();
		//		Map<String, CtClass> A3 = new HashMap<String, CtClass>();
		compileObject.add(A1);
		compileObject.add(A2);
		//		compileObject.add(A3);

		configInfo.getBeans();
		Iterator<Entry<String, Bean>> it = configInfo.getBeans().entrySet().iterator();
		//添加注册
		Entry<String, Bean> tempEn;
		ClassPool pool = ClassPool.getDefault();
		CtClass tempClazz;
		final String beans = "beans";
		while (it.hasNext()) {
			tempEn = it.next();
			tempClazz = pool.get(tempEn.getValue().getClassName());
			complete.put(tempEn.getValue().getClassName(), tempClazz);
			nick.put(tempEn.getKey(), tempEn.getValue().getClassName());
			shortName.put(ShortNameUtil.makeShortName(tempEn.getValue().getClassName()), tempEn.getValue().getClassName());

			//任务列表分组
			//			if (beans.containsKey(tempEn.getValue().getClassName())) {
			//				beans.get(tempEn.getValue().getClassName()).add(tempEn.getValue());
			//			}
			//			else
			//				beans.put(tempEn.getValue().getClassName(), new LinkedList<DefaultConfigInfo.Bean>());
			DefaultCachePoolFactory.newInstance().addNFloop4Map(false, tempEn.getValue(), beans, tempEn.getValue().getClassName(), tempEn.getValue().getType(), tempEn.getKey());
		}

		//搜索class
		for (Entry<String, CtClass> en : complete.entrySet())
			try {
				BeanFactory.addClassInfo(scanClass(Class.forName(en.getKey()), true));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		//			System.err.println(en.getKey());

		DefaultAutoLoadXmlHandler autoLoadHandler = new DefaultAutoLoadXmlHandler();
		DefaultInterceptXmlHandler interceptHandler = new DefaultInterceptXmlHandler();
		Iterator<Entry<String, CtClass>> ctEn = complete.entrySet().iterator();
		Entry<String, CtClass> tempCtEn;
		CtClass superClass;
		CtClass newClass;
		CtField[] ctFields;
		CtMethod[] ctMethods;
		HandlerInfo handlerInfo;
		Intercept intercept = null;
		boolean allIntercept;
		boolean isIntercept;
		Object interceptStr;
		Intercept tempIntercept;
		List<HandlerInfo> handlerInfos;
		int level = 0;
		while (ctEn.hasNext()) {
			level = 0;
			handlerInfos = new ArrayList<HandlerInfo>();
			tempCtEn = ctEn.next();
			superClass = tempCtEn.getValue();
			newClass = superClass.getClassPool().makeClass(superClass.getName() + Impl);
			newClass.setSuperclass(superClass);
			ctFields = superClass.getDeclaredFields();
			ctMethods = superClass.getDeclaredMethods();
			interceptStr = configInfo.getIntercepts().get(superClass.getName());
			System.err.println(superClass.getName());
			allIntercept = false;
			isIntercept = false;
			if (null != interceptStr) {
				intercept = (Intercept) interceptStr;
				if (null != (interceptStr = intercept.getMethods())) {
					if (XmlType.XmlType_All.equals(interceptStr)) {
						allIntercept = true;
					}
					isIntercept = true;
				}
			}
			Map<String, Bean> tempB = DefaultCachePoolFactory.newInstance().get4Map(beans, superClass.getName(), "field");
			//注入
			for (CtField f : ctFields)
				for (Entry<String, Bean> en : tempB.entrySet()) {
					if (f.getName().equals(en.getKey())) {
						handlerInfo = autoLoadHandler.doProcessing(cache, newClass, f, en.getValue());
						if (null == handlerInfo)
							continue;
						handlerInfos.add(handlerInfo);
						level++;
					}
				}

			for (CtMethod m : ctMethods) {//Method
				if (allIntercept || (isIntercept && interceptStr.toString().contentEquals(m.getName()))) {
					//						handlerInfos.add();
					handlerInfo = interceptHandler.doProcessing(null, newClass, m, intercept);
					if (null == handlerInfo)
						continue;
					handlerInfos.add(handlerInfo);
					newClass = handlerInfo.getNewClazz();
					level++;
				}
			}
			if (level == 0)
				A1.put(tempCtEn.getKey(), newClass);
			else
				A2.put(tempCtEn.getKey(), newClass);
			//Import
			for (HandlerInfo h : handlerInfos) {
				if (null != h.getImports())
					for (String s : h.getImports()) {
						newClass.getClassPool().importPackage(s);
					}
			}
			newClass.getClassPool().importPackage("java.lang.Exception");
			newClass.getClassPool().importPackage("java.lang.reflect.Field");
			newClass.getClassPool().importPackage("java.lang.reflect.Method");
			newClass.getClassPool().importPackage("com.ben.mc.bean.application.BeanFactory");
			newClass.getClassPool().importPackage("com.ben.mc.bean.classprocessing.ClassInfo");
			//建立构造、构造加载
			CtConstructor tempC;
			CtConstructor[] ctConstructors = superClass.getDeclaredConstructors();
			CtConstructor defauleConstructor = CtNewConstructor.defaultConstructor(newClass);
			StringBuffer sb = new StringBuffer("{");
			sb.append("super($$);");
			if (handlerInfos.size() > 0) {
				sb.append("try {");
				for (HandlerInfo h : handlerInfos) {
					if (null != h.getX())
						sb.append(h.getX());
				}
				sb.append("}catch(java.lang.Exception e){e.printStackTrace();}");
			}
			sb.append("}");
			defauleConstructor.setBody(sb.toString());
			//			defauleConstructor.addCatch("", newClass.getClassPool().get("java.lang.Exception"));
			newClass.addConstructor(defauleConstructor);
			try {
				for (CtConstructor c : ctConstructors) {
					tempC = CtNewConstructor.copy(c, newClass, null);
					tempC.setBody("{super($$);}");
					newClass.addConstructor(tempC);
				}
			} catch (DuplicateMemberException e) {
				//				e.printStackTrace();
			}
		}

		//		Object o2;
		//		anthingToClass(compileObject);

		return compileObject;
	}
}
