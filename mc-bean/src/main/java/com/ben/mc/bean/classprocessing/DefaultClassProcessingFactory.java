package com.ben.mc.bean.classprocessing;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.annotation.Register;
import com.ben.mc.bean.application.BeanFactory;
import com.ben.mc.bean.classprocessing.handler.ClassProcessingHandler;
import com.ben.mc.bean.classprocessing.handler.DefaultAutoLoadHandler;
import com.ben.mc.bean.classprocessing.handler.DefaultInterceptHandler;
import com.ben.mc.bean.classprocessing.handler.HandlerInfo;
import com.ben.mc.bean.util.ExecutorServiceFatory;
import com.ben.mc.bean.util.ShortNameUtil;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class DefaultClassProcessingFactory implements ClassProcessingFactory<CtClass> {

	public Map<String, CtClass> getCompleteClass(Set<String> clazzs,Object config) throws InterruptedException {
		final ConcurrentHashMap<String, CtClass> complete = new ConcurrentHashMap<String, CtClass>();
		final ConcurrentHashMap<String, String> nick = new ConcurrentHashMap<String, String>();
		final ConcurrentHashMap<String, String> shortName = new ConcurrentHashMap<String, String>();
		final CountDownLatch countDownLatch = new CountDownLatch(clazzs.size());
		Map<String, Map> cache = new HashMap<String, Map>();
		cache.put(ClassProcessingFactory.REGISTER_CACHE, complete);
		cache.put(ClassProcessingFactory.NICK_NAME_CACHE, nick);
		cache.put(ClassProcessingFactory.SHORT_NAME_CACHE, shortName);
		Iterator<String> it = clazzs.iterator();
		while (it.hasNext()) {
			ExecutorServiceFatory.SINGLE_EXECUTOR_SERVICE.execute(new Worker(countDownLatch, cache, it.next()));
		}
		countDownLatch.await();
		//
		try {
			List<ClassProcessingHandler> list = new ArrayList<ClassProcessingHandler>();
			list.add(new DefaultAutoLoadHandler());
			list.add(new DefaultInterceptHandler());
			doHandler(list, cache);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return complete;
	}

	class Worker implements Runnable {

		private CountDownLatch countDownLatch;
		private Map<String, Map> cache;
		private String className;
		private List<String> xmlAppendList;
		private boolean falgs;

		public Worker(CountDownLatch countDownLatch, Map<String, Map> cache, String className) {
			super();
			this.countDownLatch = countDownLatch;
			this.cache = cache;
			this.className = className;
		}

		public Worker(CountDownLatch countDownLatch, Map<String, Map> cache, List<String> xmlAppendList) {
			super();
			this.countDownLatch = countDownLatch;
			this.cache = cache;
			this.xmlAppendList = xmlAppendList;
			this.falgs = true;
		}

		public void run() {
			try {
				int count = 1;
				if (this.falgs) {
					count = xmlAppendList.size();
				}
				while (--count >= 0) {
					if (this.falgs)
						className = xmlAppendList.get(count);
					ClassPool pool = ClassPool.getDefault();
					CtClass clazz = pool.get(className);
					Object o = clazz.getAnnotation(Register.class);
					if (null == o)
						continue;
					String name = ((Register) o).value();
					if (name.length() > 0)
						cache.get(NICK_NAME_CACHE).put(name, clazz.getName());
					cache.get(REGISTER_CACHE).put(clazz.getName(), clazz);

					BeanFactory.addCache(clazz.getName(), ClassProcessingFactory.SHORT_NAME_CACHE, ShortNameUtil.makeShortName(clazz.getName()));
					//搜索class
					try {
						BeanFactory.addClassInfo(scanClass(Class.forName(className), true));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
				return;
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (null != countDownLatch)
					countDownLatch.countDown();
			}
		}
	}

	protected void doHandler(List<ClassProcessingHandler> handler, final Map<String, Map> classCache) throws Throwable {

		final Map<String, String> autoLoadClass = new ConcurrentHashMap<String, String>();
		classCache.put(ClassProcessingFactory.AUTO_LOAD_CACHE, autoLoadClass);

		Iterator<Entry<String, CtClass>> it = classCache.get(REGISTER_CACHE).entrySet().iterator();

		List<Map<String, CtClass>> compileObject = new ArrayList<Map<String, CtClass>>();
		List<HandlerInfo> handlerInfos = new ArrayList<HandlerInfo>();
		//		Set<CtField> additionalField = new HashSet<CtField>();//附加字段
		//		Set<CtMethod> additionalMethod = new HashSet<CtMethod>();//附加方法

		Map<String, CtClass> A1 = new HashMap<String, CtClass>();
		Map<String, CtClass> A2 = new HashMap<String, CtClass>();
		//		Map<String, CtClass> A3 = new HashMap<String, CtClass>();
		compileObject.add(A1);
		compileObject.add(A2);
		//		compileObject.add(A3);

		Entry<String, CtClass> en;
		int level = 0;
		while (it.hasNext()) {
			level = 0;
			en = it.next();
			CtClass tempClazz = en.getValue();
			CtClass newClazz = tempClazz.getClassPool().makeClass(tempClazz.getName() + Impl);
			newClazz.getClassPool().importPackage(en.getKey());
			newClazz.setSuperclass(tempClazz);

			//搜索Feld
			CtField[] ctFields = tempClazz.getDeclaredFields();
			//搜索Method
			//********************
			CtMethod[] ctMethods = tempClazz.getDeclaredMethods();
			HandlerInfo handlerInfo;
			for (ClassProcessingHandler<CtClass, AutoLoad, CtMember, CtClass, HandlerInfo> cph : handler) {
				for (CtField f : ctFields) {//Field
					if (null != cph.getCheck(f, ClassProcessingHandler.Field) || null != cph.getCheckII(tempClazz, ClassProcessingHandler.Type)) {
						handlerInfo = cph.doProcessing(classCache, newClazz, f);
						if (null == handlerInfo)
							continue;
						handlerInfos.add(handlerInfo);
						level++;
					}
				}
				for (CtMethod m : ctMethods) {//Method
					if (null != cph.getCheck(m, ClassProcessingHandler.Method) || null != cph.getCheckII(tempClazz, ClassProcessingHandler.Type)) {
						//						handlerInfos.add();
						handlerInfo = cph.doProcessing(classCache, newClazz, m);
						if (null == handlerInfo)
							continue;
						handlerInfos.add(handlerInfo);
						newClazz = handlerInfo.getNewClazz();
						level++;
					}
				}
			}
			if (level == 0)
				A1.put(en.getKey(), newClazz);
			else
				A2.put(en.getKey(), newClazz);

			//建立构造、构造加载
			CtConstructor tempC;
			CtConstructor[] ctConstructors = tempClazz.getDeclaredConstructors();
			CtConstructor defauleConstructor = CtNewConstructor.defaultConstructor(newClazz);
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
			//			defauleConstructor.addCatch("", newClazz.getClassPool().get("java.lang.Exception"));
			newClazz.addConstructor(defauleConstructor);
			try {
				for (CtConstructor c : ctConstructors) {
					tempC = CtNewConstructor.copy(c, newClazz, null);
					tempC.setBody("{super($$);}");
					newClazz.addConstructor(tempC);
				}
			} catch (DuplicateMemberException e) {
				//				e.printStackTrace();
			}
			//Import
			for (HandlerInfo h : handlerInfos) {
				if (null != h.getImports())
					for (String s : h.getImports()) {
						newClazz.getClassPool().importPackage(s);
					}
			}
			//Additional 附加对旬
			//			Iterator<CtField> itField = additionalField.iterator();
			//			while (itField.hasNext())
			//				newClazz.addField(itField.next());
			//			Iterator<CtMethod> itMethod = additionalMethod.iterator();
			//			while (itMethod.hasNext())
			//				newClazz.addMethod(itMethod.next());

			newClazz.getClassPool().importPackage("java.lang.Exception");
			newClazz.getClassPool().importPackage("java.lang.reflect.Field");
			newClazz.getClassPool().importPackage("java.lang.reflect.Method");
			newClazz.getClassPool().importPackage("com.ben.mc.classprocessing.BeanFactory");
			newClazz.getClassPool().importPackage("com.ben.mc.classprocessing.ClassInfo");
			//反编查看
			//			try {
			//				tempClazz.writeFile("C:/Users/Ben/Desktop");
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			}
		}
		anthingToClass(compileObject);
	}

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

	private ClassInfo scanClass(Class c, boolean isConcurrent) {
		Map<String, Field> fields = null;
		Map<String, Method> methods = null;
		if (isConcurrent) {
			final Map<String, Field> f = new ConcurrentHashMap<String, Field>();
			final Map<String, Method> m = new ConcurrentHashMap<String, Method>();
			fields = f;
			methods = m;
		} else {
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
