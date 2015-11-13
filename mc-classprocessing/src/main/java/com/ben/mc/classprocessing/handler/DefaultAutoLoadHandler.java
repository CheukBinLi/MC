package com.ben.mc.classprocessing.handler;

import java.lang.reflect.Modifier;
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
import com.ben.mc.util.ShortNameUtil;

public class DefaultAutoLoadHandler extends AbstractClassProcessingHandler<CtClass, AutoLoad> {

	@SuppressWarnings("unchecked")
	@Override
	public CtMember doProcessing(final Map<String, Map> cache, CtClass newClass, CtMember additional) throws Throwable {
		CtField o = (CtField) additional;
		Iterator<Entry<String, CtClass>> autoIt = cache.get(ClassProcessingFactory.REGISTER_CACHE).entrySet().iterator();
		Entry<String, CtClass> tempEn;
		String nick;
		//命名匹配
		if (this.a.value().length() > 0) {
			System.out.println(this.a.value());
			Object x = cache.get(ClassProcessingFactory.NICK_NAME_CACHE);
			nick = cache.get(ClassProcessingFactory.NICK_NAME_CACHE).get(this.a.value()).toString();
			if (null != nick) {
				o = CtField.make(makeField(o, ((CtClass) cache.get(ClassProcessingFactory.REGISTER_CACHE).get(nick)).getName()), newClass);
				return o;
			}

		}
		//对象名匹配
		nick = BeanFactory.getFullName(ShortNameUtil.objectHumpNameUpper(o.getName()));
		if (nick != null) {
			o = CtField.make(makeField(o, nick), newClass);
			System.err.println("ooooo");
			return o;
		}

		while (autoIt.hasNext()) {
			tempEn = autoIt.next();
			//模糊匹配
			if (tempEn.getValue().subtypeOf(o.getType())) {
				cache.get(ClassProcessingFactory.AUTO_LOAD_CACHE).put(o.getType().getName(), tempEn.getValue().getName());//自动装载
				//				String a = String.format("%s %s %s=new %s%s();", Modifier.toString(o.getModifiers()), o.getType().getName(), o.getName(), tempEn.getValue().getName(), ClassProcessingFactory.Impl);
				//				System.err.println(a);
				o = CtField.make(makeField(o, tempEn.getValue().getName()), newClass);
				//				newClass.addField(o);
				return o;
			}
		}
		throw new Throwable("can find matching class !");
	}

	protected String makeField(CtField o, String classImpl) throws NotFoundException {
		return String.format("%s %s %s=new %s%s();", Modifier.toString(o.getModifiers()), o.getType().getName(), o.getName(), classImpl, ClassProcessingFactory.Impl);
	}

	@Override
	public Class<AutoLoad> handlerClass() {
		return AutoLoad.class;
	}

}
