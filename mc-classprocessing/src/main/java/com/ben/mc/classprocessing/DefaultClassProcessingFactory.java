package com.ben.mc.classprocessing;

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

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtNewConstructor;
import javassist.NotFoundException;
import javassist.bytecode.DuplicateMemberException;

import com.ben.mc.annotation.AutoLoad;
import com.ben.mc.annotation.Register;
import com.ben.mc.classprocessing.handler.ClassProcessingHandler;
import com.ben.mc.classprocessing.handler.DefaultAutoLoadHandler;
import com.ben.mc.classprocessing.handler.HandlerInfo;
import com.ben.mc.util.ExecutorServiceFatory;
import com.ben.mc.util.ShortNameUtil;

public abstract class DefaultClassProcessingFactory implements ClassProcessingFactory<CtClass> {

	public Map<String, CtClass> getCompleteClass(Set<String> clazzs, List<String> xmlAppendList) throws InterruptedException {
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
		if (null != xmlAppendList)
			ExecutorServiceFatory.SINGLE_EXECUTOR_SERVICE.execute(new Worker(null, cache, xmlAppendList));
		countDownLatch.await();
		//
		try {
			doHandler(null, cache);
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
		@SuppressWarnings("rawtypes")
		private Map<String, Map> cache;
		private String className;
		private List<String> xmlAppendList;
		private boolean falgs;

		@SuppressWarnings("rawtypes")
		public Worker(CountDownLatch countDownLatch, Map<String, Map> cache, String className) {
			super();
			this.countDownLatch = countDownLatch;
			this.cache = cache;
			this.className = className;
		}

		@SuppressWarnings("rawtypes")
		public Worker(CountDownLatch countDownLatch, Map<String, Map> cache, List<String> xmlAppendList) {
			super();
			this.countDownLatch = countDownLatch;
			this.cache = cache;
			this.xmlAppendList = xmlAppendList;
			this.falgs = true;
		}

		@SuppressWarnings("unchecked")
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
					Object x = clazz;
					System.err.println("xxxxxx:" + x);
					//					cache.get(SHORT_NAME_CACHE).put(clazz.getName(), ClassProcessingFactory.SHORT_NAME_CACHE, ShortNameUtil.makeShortName(clazz.getName()));

					BeanFactory.addCache(clazz.getName(), ClassProcessingFactory.SHORT_NAME_CACHE, ShortNameUtil.makeShortName(clazz.getName()));

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

	@SuppressWarnings("rawtypes")
	protected void doHandler(List<ClassProcessingHandler> handler, final Map<String, Map> classCache) throws Throwable {

		final Map<String, String> autoLoadClass = new ConcurrentHashMap<String, String>();
		classCache.put(ClassProcessingFactory.AUTO_LOAD_CACHE, autoLoadClass);

		Iterator<Entry<String, CtClass>> it = classCache.get(REGISTER_CACHE).entrySet().iterator();

		List<Map<String, CtClass>> compileObject = new ArrayList<Map<String, CtClass>>();
		List<HandlerInfo> handlerInfos = new ArrayList<HandlerInfo>();

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

			//搜索Method

			//构造加载、引包

			//建立构造
			CtField[] ctFields = tempClazz.getDeclaredFields();
			//			CtField newField;
			ClassProcessingHandler<CtClass, AutoLoad, CtMember, HandlerInfo> cph = new DefaultAutoLoadHandler();
			//			<O, A, I, R> 
			for (CtField f : ctFields) {
				if (null != cph.getCheck(f)) {
					//					newClazz.addField((CtField) cph.doProcessing(classCache, newClazz, f));
					//					DefaultAutoLoadHandler.AutoLoadList a = cph.doProcessing(classCache, newClazz, f);
					//					imports.add(a.getImports());
					//					injections.add(a.getField());
					handlerInfos.add(cph.doProcessing(classCache, newClazz, f));
					level++;
				}
			}
			if (level == 0)
				A1.put(en.getKey(), newClazz);
			else
				A2.put(en.getKey(), newClazz);
			//构造块
			CtConstructor tempC;
			CtConstructor[] ctConstructors = tempClazz.getDeclaredConstructors();
			CtConstructor defauleConstructor = CtNewConstructor.defaultConstructor(newClazz);
			StringBuffer sb = new StringBuffer("{");
			sb.append("super($$);");
			if (handlerInfos.size() > 0) {
				sb.append("try {");
				for (HandlerInfo h : handlerInfos) {
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
				for (String s : h.getImports())
					newClazz.getClassPool().importPackage(s);
			}
			newClazz.getClassPool().importPackage("java.lang.Exception");
			newClazz.getClassPool().importPackage("java.lang.reflect.Field");
			//反编查看
			try {
				tempClazz.writeFile("C:/Users/Ben/Desktop");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		anthingToClass(compileObject);
	}

	protected void anthingToClass(List<Map<String, CtClass>> compileObject) throws CannotCompileException {
		for (int i = 0, len = compileObject.size(); i < len; i++) {
			for (Entry<String, CtClass> en : compileObject.get(i).entrySet()) {
				@SuppressWarnings("rawtypes")
				final Class c = en.getValue().toClass();
				BeanFactory.addBean(c, FULL_NAME_BEAN, en.getKey());
				BeanFactory.addBean(c, NICK_NAME_BEAN, ShortNameUtil.makeLowerHumpNameShortName(en.getKey()));
				BeanFactory.addBean(c, SHORT_NAME_BEAN, ShortNameUtil.makeShortName(en.getKey()));
				//搜索class

				try {
					en.getValue().writeFile("C:/Users/Ben/Desktop");
				} catch (IOException e) {
					// TODO Auto-generated catch block
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
			m.getParameters()
			methods.put(m.getName(), m)
		}
		ClassInfo ci = new ClassInfo(c.getName(), c, fields, methods);

		return null;
	}
	//	static {
	//		{
	//			super($$);
	//			try {
	//				Field fields = this.getClass().getSuperclass().getDeclaredField("autoLoadTestImpl");
	//				fields.setAccessible(true);
	//				fields.set(this, new com.ben.mc.AnthingTest.AutoLoadTestImpl$MC_IMPL());
	//			} catch (java.lang.Exception e) {
	//			}
	//		}
	//
	//	}
}
