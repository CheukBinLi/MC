package com.ben.mc.classprocessing;

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
import com.ben.mc.util.ExecutorServiceFatory;
import com.ben.mc.util.ShortNameUtil;

public abstract class DefaultClassProcessingFactory implements ClassProcessingFactory<CtClass> {

	public abstract ClazzInfo classProcessing(Class clazz) throws Throwable;

	public abstract ClazzInfo classProcessing(String clazz) throws Throwable;

	@Override
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
		@Override
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
			//nickName
			//			Register register = (Register) tempClazz.getAnnotation(Register.class);
			//			if (null != register.value())
			//				nickNameClass.put(register.value(), newClazz);

			//构造块
			CtConstructor tempC;
			CtConstructor[] ctConstructors = tempClazz.getDeclaredConstructors();
			newClazz.addConstructor(CtNewConstructor.defaultConstructor(newClazz));
			try {
				for (CtConstructor c : ctConstructors) {
					tempC = CtNewConstructor.copy(c, newClazz, null);
					tempC.setBody("{super($$);}");
					newClazz.addConstructor(tempC);
				}
			} catch (DuplicateMemberException e) {
				//				e.printStackTrace();
			}
			//FIELD 注入
			CtField[] ctFields = tempClazz.getDeclaredFields();
			CtField newField;
			ClassProcessingHandler<CtClass, AutoLoad, CtMember> cph = new DefaultAutoLoadHandler();
			for (CtField f : ctFields) {
				if (null != cph.getCheck(f)) {
					newClazz.addField((CtField) cph.doProcessing(classCache, newClazz, f));
				}
				//				Object o = f.getAnnotation(AutoLoad.class);
				//				if (null != o) {
				//					Iterator<Entry<String, CtClass>> autoIt = clazzs.entrySet().iterator();
				//					System.err.println(f.getType().getName());
				//					Entry<String, CtClass> tempEn;
				//					while (autoIt.hasNext()) {
				//						tempEn = autoIt.next();
				//						//模糊匹配
				//						if (tempEn.getValue().subtypeOf(f.getType())) {
				//							autoLoadClass.put(f.getType().getName(), tempEn.getValue().getName());//自动装载
				//							String a = String.format("%s %s %s=new %s%s();", Modifier.toString(f.getModifiers()), f.getType().getName(), f.getName(), tempEn.getValue().getName(), Impl);
				//							System.err.println(a);
				//							newClazz.getClassPool().importPackage(tempEn.getValue().getName());//import
				//							newField = CtField.make(a, newClazz);
				//							newClazz.addField(newField);
				//							level = 1;
				//							break;
				//							//							System.out.println("bbbbbbbbbbbbbbb:" + tempEn.getValue().getName());
				//							//						CtClass a1 = f.getType();
				//							//						CtClass a2 = tempEn.getValue();
				//							//						System.err.println(String.format("%s----subType----:%s  : %s", f.getName(), tempEn.getValue().getName(), a2.subtypeOf(a1)));
				//							//						System.err.println(String.format("%s----superType----:%s  : %s", f.getName(), tempEn.getValue().getName(), a2.subtypeOf(a1)));
				//							//						f.getType().getClass().isAssignableFrom(tempEn.getValue().getClass());
				//						}
				//					}
				//
				//				}
			}
			if (level == 0)
				A1.put(en.getKey(), newClazz);
			else
				A2.put(en.getKey(), newClazz);
			//反编查看
			//			try {
			//				newClazz.writeFile("C:/Users/Ben/Desktop");
			//			} catch (IOException e) {
			//				e.printStackTrace();
			//			}
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
			}
		}

	}
}
