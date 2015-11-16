package com.ben.mc.classprocessing.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.NotFoundException;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.classprocessing.BeanFactory;
import com.ben.mc.classprocessing.ClassProcessingFactory;
import com.ben.mc.classprocessing.DefaultClassProcessingFactory;
import com.ben.mc.util.ShortNameUtil;

/***
 * 
 * Copyright 2015    ZHOU.BING.LI Individual All
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
 * @see 默认  自动装载处理器
 *
 */
public class DefaultAutoLoadHandler extends AbstractClassProcessingHandler<CtClass, AutoLoad, DefaultAutoLoadHandler.AutoLoadList> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AutoLoadList doProcessing(final Map<String, Map> cache, CtClass newClass, CtMember additional) throws Throwable {
		Object bx = newClass.getConstructors();
		CtField o = (CtField) additional;
		Iterator<Entry<String, CtClass>> autoIt = cache.get(ClassProcessingFactory.REGISTER_CACHE).entrySet().iterator();
		Entry<String, CtClass> tempEn;
		String nick;
		//命名匹配
		if (this.a.value().length() > 0) {
			System.out.println(this.a.value());
			nick = cache.get(ClassProcessingFactory.NICK_NAME_CACHE).get(this.a.value()).toString();
			if (null != nick) {
				//				o = CtField.make(makeField(o, ((CtClass) cache.get(ClassProcessingFactory.REGISTER_CACHE).get(nick)).getName()), newClass);
				//				return o;
				nick = ((CtClass) cache.get(ClassProcessingFactory.REGISTER_CACHE).get(nick)).getName();
				//				return new AutoLoadList(makeField(o, nick), nick);
				StringBuffer sb = new StringBuffer();
				sb.append("Field fields = this.getClass().getSuperclass().getDeclaredField(\"").append(o.getName()).append("\");");
				sb.append("fields.setAccessible(true);");
				sb.append("fields.set(this, new ").append(nick + DefaultClassProcessingFactory.Impl).append("());");
				System.err.println(sb.toString());
				return new AutoLoadList(sb.toString(), nick);
			}

		}
		//对象名匹配
		nick = BeanFactory.getFullName(ShortNameUtil.objectHumpNameUpper(o.getName()));
		if (nick != null) {
			//			o = CtField.make(makeField(o, nick), newClass);
			//			return o;
			//***************
			//return new AutoLoadList(makeField(o, nick), nick);
			StringBuffer sb = new StringBuffer();
			sb.append("Field fields = this.getClass().getSuperclass().getDeclaredField(\"").append(o.getName()).append("\");");
			sb.append("fields.setAccessible(true);");
			sb.append("fields.set(this, new ").append(nick + DefaultClassProcessingFactory.Impl).append("());");
			System.err.println(sb.toString());
			return new AutoLoadList(sb.toString(), nick);
		}

		while (autoIt.hasNext()) {
			tempEn = autoIt.next();
			//模糊匹配
			if (tempEn.getValue().subtypeOf(o.getType())) {
				cache.get(ClassProcessingFactory.AUTO_LOAD_CACHE).put(o.getType().getName(), tempEn.getValue().getName());//自动装载
				//				String a = String.format("%s %s %s=new %s%s();", Modifier.toString(o.getModifiers()), o.getType().getName(), o.getName(), tempEn.getValue().getName(), ClassProcessingFactory.Impl);
				//				System.err.println(a);
				//				Annotation a = new Annotation("java.lang.Override", o.getFieldInfo().getConstPool());
				//				AnnotationsAttribute aa = new AnnotationsAttribute(o.getFieldInfo().getConstPool(), AnnotationsAttribute.visibleTag);
				//				aa.addAnnotation(a);
				//				o.getFieldInfo().addAttribute(aa);
				//				newClass.addField(o);
				//				o = CtField.make(makeField(o, tempEn.getValue().getName()), newClass);
				//				return o;
				//**************
				//				return new AutoLoadList(makeField(o, tempEn.getValue().getName()), tempEn.getValue().getName());
				//				Field fields = this.getClass().getSuperclass().getDeclaredField("a1");
				//				fields.setAccessible(true);
				//				fields.set(this, new AA());
				StringBuffer sb = new StringBuffer();
				sb.append("Field fields = this.getClass().getSuperclass().getDeclaredField(\"").append(o.getName()).append("\");");
				sb.append("fields.setAccessible(true);");
				sb.append("fields.set(this, new ").append(tempEn.getValue().getName() + DefaultClassProcessingFactory.Impl).append("());");
				System.err.println(sb.toString());
				return new AutoLoadList(sb.toString(), tempEn.getValue().getName());
			}
		}
		throw new Throwable("can find matching class !");
	}

	protected String makeField(CtField o, String implClassName) throws NotFoundException {
		//		return String.format("%s %s %s=new %s%s();", Modifier.toString(o.getModifiers()), o.getType().getName(), o.getName(), classImpl, ClassProcessingFactory.Impl);
		return String.format(" %s=new %s%s();", o.getName(), implClassName, ClassProcessingFactory.Impl);
	}

	@Override
	public Class<AutoLoad> handlerClass() {
		return AutoLoad.class;
	}

	public static class AutoLoadList {
		private String field;
		private String imports;

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getImports() {
			return imports;
		}

		public void setImports(String imports) {
			this.imports = imports;
		}

		public AutoLoadList(String field, String imports) {
			super();
			this.field = field;
			this.imports = imports;
		}

	}

}
