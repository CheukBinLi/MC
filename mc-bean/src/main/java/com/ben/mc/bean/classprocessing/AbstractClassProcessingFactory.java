package com.ben.mc.bean.classprocessing;

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

	static final ClassLoader cl = Thread.currentThread().getContextClassLoader();
	//	static final ClassLoader cl = ClassLoader.getSystemClassLoader().getParent();
	static Method addClass = null;

	static {
		Method[] mx = java.lang.ClassLoader.class.getDeclaredMethods();
		for (Method m : mx)
			if ("addClass".contentEquals(m.getName())) {
				addClass = m;
				addClass.setAccessible(true);
				break;
			}
	}

	public void anthingToClass(List<Map<String, CtClass>> compileObject, boolean initSystemClassLoader) throws CannotCompileException {
		//		LinkedList<Entry<String, CtClass>> errorQueue = new LinkedList<Entry<String, CtClass>>();
		if (null == compileObject)
			return;
		for (int i = 0, len = compileObject.size(); i < len; i++) {
			for (Entry<String, CtClass> en : compileObject.get(i).entrySet()) {
				final Class c = en.getValue().toClass();
				BeanFactory.addBean(c, FULL_NAME_BEAN, en.getKey());
				BeanFactory.addBean(c, NICK_NAME_BEAN, ShortNameUtil.makeLowerHumpNameShortName(en.getKey()));
				BeanFactory.addBean(c, SHORT_NAME_BEAN, ShortNameUtil.makeShortName(en.getKey()));
//				System.err.println(ShortNameUtil.makeShortName(en.getKey()));

				if (initSystemClassLoader)
					if (null != addClass)
						try {
//							System.out.println("addClass:" + c.getName());
							addClass.invoke(cl, c);
						} catch (Exception e1) {
							e1.printStackTrace();
							throw new CannotCompileException(e1);
						}
					else
						throw new CannotCompileException("加载失败");

				//搜索class
				//				BeanFactory.addClassInfo(scanClass(c, false ));
				//				//反编查看
				//				try {
				//					en.getValue().writeFile("C:/Users/Ben/Desktop");
				//				} catch (Exception e) {
				//					//					errorQueue.add(en);
				//					e.printStackTrace();
				//				}
			}
		}
		//		Entry<String, CtClass> temp = null;
		//		while (errorQueue.size() > 0) {
		//			temp = errorQueue.removeFirst();
		//			final Class c = temp.getValue().toClass();
		//			BeanFactory.addBean(c, FULL_NAME_BEAN, temp.getKey());
		//			BeanFactory.addBean(c, NICK_NAME_BEAN, ShortNameUtil.makeLowerHumpNameShortName(temp.getKey()));
		//			BeanFactory.addBean(c, SHORT_NAME_BEAN, ShortNameUtil.makeShortName(temp.getKey()));
		//			//搜索class
		//			//				BeanFactory.addClassInfo(scanClass(c, false ));
		//			//反编查看
		//			//			try {
		//			//				temp.getValue().writeFile("C:/Users/Ben/Desktop");
		//			//			} catch (IOException e) {
		//			//				errorQueue.add(temp);
		//			//				//					e.printStackTrace();
		//			//			}
		//		}
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
