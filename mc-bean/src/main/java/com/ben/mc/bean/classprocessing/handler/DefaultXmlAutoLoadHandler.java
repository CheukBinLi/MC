package com.ben.mc.bean.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.classprocessing.ClassProcessingFactory;
import com.ben.mc.bean.classprocessing.DefaultClassProcessingFactory;
import com.ben.mc.bean.util.ShortNameUtil;
import com.ben.mc.bean.xml.DefaultConfigInfo;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.NotFoundException;

/***
 * 
 * Copyright 2015 ZHOU.BING.LI Individual All
 * 
 * ALL RIGHT RESERVED
 * 
 * CREATE ON 2015年11月13日 下午5:25:57
 * 
 * EMAIL:20796698@QQ.COM
 * 
 * GITHUB:https://github.com/fdisk123
 * 
 * @author ZHUO.BIN.LI
 * 
 * @see 默认 自动装载处理器
 *
 */
public class DefaultXmlAutoLoadHandler extends AbstractClassProcessingHandler<CtClass, AutoLoad> {

	public Set<Integer> thisType() {
		return new HashSet<Integer>(Arrays.asList(Field));
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public HandlerInfo doProcessing(final Map<String, Map> cache, CtClass newClass, CtMember additional, Object config) throws Throwable {
		DefaultConfigInfo configInfo = (DefaultConfigInfo) cache.get(ClassProcessingFactory.XML_CONFIG_CACHE).get(ClassProcessingFactory.XML_CONFIG_CACHE);

		return null;
	}

	protected String makeField(CtField o, String implClassName) throws NotFoundException {
		//		return String.format("%s %s %s=new %s%s();", Modifier.toSring(o.getModifiers()), o.getType().getName(), o.getName(), classImpl, ClassProcessingFactory.Impl);
		return String.format(" %s=new %s%s();", o.getName(), implClassName, ClassProcessingFactory.Impl);
	}

	public Class<AutoLoad> handlerClass() {
		return AutoLoad.class;
	}

}
