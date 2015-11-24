package com.ben.mc.bean.classprocessing;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.util.ShortNameUtil;

import javassist.CannotCompileException;
import javassist.CtClass;

@SuppressWarnings("rawtypes")
public abstract class AbstractClassProcessingFactory<C> implements ClassProcessingFactory<C> {

	protected void anthingToClass(List<Map<String, CtClass>> compileObject) throws CannotCompileException {
		for (int i = 0, len = compileObject.size(); i < len; i++) {
			for (Entry<String, CtClass> en : compileObject.get(i).entrySet()) {
				final Class c = en.getValue().toClass();
				BeanFactory.addBean(c, FULL_NAME_BEAN, en.getKey());
				BeanFactory.addBean(c, NICK_NAME_BEAN, ShortNameUtil.makeLowerHumpNameShortName(en.getKey()));
				BeanFactory.addBean(c, SHORT_NAME_BEAN, ShortNameUtil.makeShortName(en.getKey()));
				//搜索class
				//				BeanFactory.addClassInfo(scanClass(c, false ));
				//反编查看
				try {
					en.getValue().writeFile("C:/Users/Ben/Desktop");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected ClassInfo scanClass(Class c, boolean isConcurrent) {
		Map<String, Field> fields = null;
		Map<String, Method> methods = null;
		if (isConcurrent) {
			final Map<String, Field> f = new ConcurrentHashMap<String, Field>();
			final Map<String, Method> m = new ConcurrentHashMap<String, Method>();
			fields = f;
			methods = m;
		}
		else {
			final Map<String, Field> f = new HashMap<String, Field>();
			final Map<String, Method> m = new HashMap<String, Method>();
			fields = f;
			methods = m;
		}
		Field[] fs = c.getDeclaredFields();
		Method[] ms = c.getDeclaredMethods();
		for (Field f : fs) {
			f.setAccessible(true);
			fields.put(f.getName(), f);
		}
		for (Method m : ms) {
			methods.put(ClassInfo.getMethod(m), m);
		}
		ClassInfo ci = new ClassInfo(c.getName() + Impl, c, fields, methods);
		return ci;
	}
}
