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
		//命名匹配
		if (this.a.value().length() > 0) {
			nick = cache.get(ClassProcessingFactory.NICK_NAME_CACHE).get(this.a.value()).toString();
			if (null != nick) {
				nick = ((CtClass) cache.get(ClassProcessingFactory.REGISTER_CACHE).get(nick)).getName();
				StringBuffer sb = new StringBuffer();
				sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
				//				sb.append("field.set(this, BeanFactory.getBean(\"").append(nick).append("\"));");
				sb.append("field.set(this, new ").append(nick + DefaultClassProcessingFactory.Impl).append("());");
				return new HandlerInfo(sb.toString(), nick);

			}

		}
		//对象名匹配
		nick = BeanFactory.getFullName(ShortNameUtil.objectHumpNameUpper(o.getName()));
		if (nick != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
			//			sb.append("field.set(this, BeanFactory.getBean(\"").append(nick).append("\"));");
			sb.append("field.set(this, new ").append(nick + DefaultClassProcessingFactory.Impl).append("());");
			return new HandlerInfo(sb.toString(), nick);
		}

		while (autoIt.hasNext()) {
			tempEn = autoIt.next();
			//模糊匹配
			if (tempEn.getValue().subtypeOf(o.getType())) {
				cache.get(ClassProcessingFactory.AUTO_LOAD_CACHE).put(o.getType().getName(), tempEn.getValue().getName());//自动装载
				StringBuffer sb = new StringBuffer();
				sb.append("java.lang.reflect.Field field = BeanFactory.getClassInfoField(\"").append(newClass.getName()).append("\",\"").append(o.getName()).append("\");");
				//				sb.append("field.set(this, BeanFactory.getBean(\"").append(tempEn.getValue().getName()).append("\"));");
				sb.append("field.set(this, new ").append(tempEn.getValue().getName() + DefaultClassProcessingFactory.Impl).append("());");
				return new HandlerInfo(sb.toString(), tempEn.getValue().getName());
			}
		}
		throw new Throwable("can find matching class !");
	}

	protected String makeField(CtField o, String implClassName) throws NotFoundException {
		//		return String.format("%s %s %s=new %s%s();", Modifier.toSring(o.getModifiers()), o.getType().getName(), o.getName(), classImpl, ClassProcessingFactory.Impl);
		return String.format(" %s=new %s%s();", o.getName(), implClassName, ClassProcessingFactory.Impl);
	}

	public Class<AutoLoad> handlerClass() {
		return AutoLoad.class;
	}

	//	@Override
	//	public com.ben.mc.classprocessing.handler.ClassProcessingHandler.scope getScope() {
	//		return scope.TypeOrField;
	//	}

	public static void main(String[] args) {

		//		int a1 = 8 | 2 | 4;
		//		int a2 = 2 | 4 | 10;
		//		int a3 = 2 | 4 | 5 | 6;
		//		System.out.println(a1);
		//		System.out.println(a2);
		//		System.out.println(a3);
		int ax1 = 0x00000001;
		int ax2 = 0x00000002;
		int ax3 = 0x00000004;

		int ax4 = 0x00000008;
		int ax5 = 0x00000010;
		int ax6 = 0x00000040;

		System.err.println(ax4 + ax6);
		System.err.println(ax4 + ax5);
		System.err.println(ax4 + ax3);

	}

	//	static {
	//		java.lang.reflect.Field field = BeanFactory.getClassInfoField("com.ben.mc.AnthingTest.mc.scan.IocTest1$MC_IMPL", "autoLoadTestImpl");
	//		try {
	//			field.set("", BeanFactory.getBean("com.ben.mc.AnthingTest.mc.scan.AutoLoadTestImpl"));
	//		} catch (IllegalArgumentException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (IllegalAccessException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (InstantiationException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}

}
