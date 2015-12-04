package com.ben.mc.bean.classprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.classprocessing.handler.DefaultAutoLoadXmlHandler;
import com.ben.mc.bean.classprocessing.handler.DefaultInterceptXmlHandler;
import com.ben.mc.bean.classprocessing.handler.HandlerInfo;
import com.ben.mc.bean.util.ShortNameUtil;
import com.ben.mc.bean.xml.DefaultConfigInfo;
import com.ben.mc.bean.xml.DefaultConfigInfo.Bean;
import com.ben.mc.bean.xml.DefaultConfigInfo.Intercept;
import com.ben.mc.bean.xml.XmlType;
import com.ben.mc.cache.DefaultCachePoolFactory;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

@SuppressWarnings({ "rawtypes", "unused" })
public class DefaultClassProcessingXmlFactory extends AbstractClassProcessingFactory<CreateClassInfo> {

	public CreateClassInfo getCompleteClass(Set<String> clazzs, Object config) throws Throwable {
		final ConcurrentHashMap<String, CtClass> complete = new ConcurrentHashMap<String, CtClass>();
		final ConcurrentHashMap<String, String> nick = new ConcurrentHashMap<String, String>();
		final ConcurrentHashMap<String, String> shortName = new ConcurrentHashMap<String, String>();

		final DefaultConfigInfo configInfo = (DefaultConfigInfo) config;

		final CreateClassInfo result = new CreateClassInfo();

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
		final String beans = "beans";
		while (it.hasNext()) {
			tempEn = it.next();
			if (null != BeanFactory.getBean(tempEn.getValue().getClassName()))
				continue;
			tempClazz = pool.get(tempEn.getValue().getClassName());
			complete.put(tempEn.getValue().getClassName(), tempClazz);
			nick.put(tempEn.getKey(), tempEn.getValue().getClassName());
			shortName.put(ShortNameUtil.makeShortName(tempEn.getValue().getClassName()), tempEn.getValue().getClassName());
			DefaultCachePoolFactory.newInstance().addNFloop4Map(false, tempEn.getValue(), beans, tempEn.getValue().getClassName(), tempEn.getValue().getType(), tempEn.getKey());
		}
		for (Entry<String, CtClass> en : complete.entrySet())
			try {
				BeanFactory.addClassInfo(scanClass(Class.forName(en.getKey()), true));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
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
		Set<String> interecptMethodName = null;
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
			//			System.err.println(superClass.getName());
			allIntercept = false;
			isIntercept = false;
			if (null != interceptStr) {
				intercept = (Intercept) interceptStr;
				if (null != (interceptStr = intercept.getMethods())) {
					if (XmlType.XmlType_All.equals(interceptStr)) {
						allIntercept = true;
					}
					isIntercept = true;
					interecptMethodName = new HashSet<String>(Arrays.asList(interceptStr.toString().split(",")));
				}
			}
			Map<String, Bean> tempB = DefaultCachePoolFactory.newInstance().get4Map(beans, superClass.getName(), "field");
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
				if (allIntercept || (isIntercept && null != interecptMethodName && interecptMethodName.contains(m.getName()))) {
					//						handlerInfos.add();
					handlerInfo = interceptHandler.doProcessing(null, newClass, m, intercept);
					if (null == handlerInfo)
						continue;
					handlerInfos.add(handlerInfo);
					newClass = handlerInfo.getNewClazz();
					level++;
				}
			}
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
			if (level == 0)
				result.addFirstQueue(new DefaultTempClass(superClass, newClass));
			else
				result.addSecondQueue(new DefaultTempClass(superClass, newClass, sb.toString(), null));

		}
		return result;
	}
}
