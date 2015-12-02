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
public class DefaultAutoLoadHandler extends AbstractClassProcessingHandler<CtClass, AutoLoad> {

	public Set<Integer> thisType() {
		return new HashSet<Integer>(Arrays.asList(Field));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HandlerInfo doProcessing(final Map<String, Map> cache, CtClass newClass, CtMember additional, Object config) throws Throwable {
		if (!(additional instanceof CtField))
			return null;
		CtField o = (CtField) additional;
		Iterator<Entry<String, CtClass>> autoIt = cache.get(ClassProcessingFactory.REGISTER_CACHE).entrySet().iterator();
		Entry<String, CtClass> tempEn;
		String nick;
		if (this.a.value().length() > 0) {
			nick = cache.get(ClassProcessingFactory.NICK_NAME_CACHE).get(this.a.value()).toString();
			if (null != nick) {
				nick = ((CtClass) cache.get(ClassProcessingFactory.REGISTER_CACHE).get(nick)).getName();
				StringBuffer sb = new StringBuffer();
				sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
				sb.append("field.set(this, new ").append(nick + DefaultClassProcessingFactory.Impl).append("());");
				return new HandlerInfo(sb.toString(), nick);

			}

		}
		nick = BeanFactory.getFullName(ShortNameUtil.objectHumpNameUpper(o.getName()));
		if (nick != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
			sb.append("field.set(this, new ").append(nick + DefaultClassProcessingFactory.Impl).append("());");
			return new HandlerInfo(sb.toString(), nick);
		}

		while (autoIt.hasNext()) {
			tempEn = autoIt.next();
			if (tempEn.getValue().subtypeOf(o.getType())) {
				cache.get(ClassProcessingFactory.AUTO_LOAD_CACHE).put(o.getType().getName(), tempEn.getValue().getName());//自动装载
				StringBuffer sb = new StringBuffer();
				sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
				sb.append("field.set(this, new ").append(tempEn.getValue().getName() + DefaultClassProcessingFactory.Impl).append("());");
				return new HandlerInfo(sb.toString(), tempEn.getValue().getName());
			}
		}
		throw new Throwable("can find matching class !");
	}

	public Class<AutoLoad> handlerClass() {
		return AutoLoad.class;
	}

}
