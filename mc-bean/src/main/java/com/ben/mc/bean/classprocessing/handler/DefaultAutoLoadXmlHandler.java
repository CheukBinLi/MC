package com.ben.mc.bean.classprocessing.handler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ben.mc.bean.classprocessing.ClassProcessingFactory;
import com.ben.mc.bean.classprocessing.DefaultClassProcessingXmlFactory;
import com.ben.mc.bean.xml.DefaultConfigInfo;
import com.ben.mc.bean.xml.DefaultConfigInfo.Bean;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;

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
public class DefaultAutoLoadXmlHandler extends AbstractClassProcessingHandler<CtClass, Object> {

	public Set<Integer> thisType() {
		return new HashSet<Integer>(Arrays.asList(Field));
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	public HandlerInfo doProcessing(final Map<String, Map> cache, CtClass newClass, CtMember additional, Object config) throws Throwable {
		if (!(additional instanceof CtField))
			return null;
		CtField o = (CtField) additional;
		DefaultConfigInfo configInfo = (DefaultConfigInfo) cache.get(ClassProcessingFactory.XML_CONFIG_CACHE).get(ClassProcessingFactory.XML_CONFIG_CACHE);
		Bean bean = (Bean) config;
		StringBuffer sb = new StringBuffer();
		String refName = null;
		if (null != (refName = cache.get(ClassProcessingFactory.NICK_NAME_CACHE).get(bean.getRef()).toString()) || null != (refName = ((CtClass) cache.get(ClassProcessingFactory.REGISTER_CACHE).get(bean.getRef())).getName())) {
			sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
			sb.append("field.set(this, new ").append(refName + DefaultClassProcessingXmlFactory.Impl).append("());");
		}
		else
			throw new Throwable(String.format("%s没有注册实例，请在Ban中配置相关参数。 ", bean.getRef()));

		return new HandlerInfo(sb.toString(), newClass, additional, null);
	}

	public Class<Object> handlerClass() {
		return Object.class;
	}

}
