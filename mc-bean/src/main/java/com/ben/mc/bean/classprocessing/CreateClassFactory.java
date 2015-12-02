package com.ben.mc.bean.classprocessing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RecursiveAction;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;
import javassist.bytecode.DuplicateMemberException;

public class CreateClassFactory {

	private transient BlockingQueue<DefaultTempClass> errorQueue = new LinkedBlockingQueue<DefaultTempClass>();

	private static CreateClassFactory newInstance = new CreateClassFactory();

	private boolean initSystemClassLoader;

	public static CreateClassFactory newInstance() {
		return newInstance;
	}

	public void create(final CreateClassInfo classInfo, final boolean initSystemClassLoader) {
		DefaultTempClass tempClass;
		for (int i = 0, len = classInfo.getFirstQueue().size(); i < len; i++) {
			try {
				tempClass = classInfo.getFirstQueue().get(i);
				AbstractClassProcessingFactory.anthingToClass(tempClass.getNewClazz(), tempClass.getSuperClazz().getName(), initSystemClassLoader);
			} catch (CannotCompileException e) {
				e.printStackTrace();
			}
		}
		errorQueue.addAll(classInfo.getSecondQueue());
		while (errorQueue.size() > 0) {
			tempClass = errorQueue.poll();
			try {
				CtConstructor tempC;
				CtClass newClazz = tempClass.getNewClazz();
				CtConstructor[] ctConstructors = tempClass.getSuperClazz().getDeclaredConstructors();
				CtConstructor defauleConstructor = CtNewConstructor.defaultConstructor(newClazz);
				defauleConstructor.setBody(tempClass.getConstructor());
				newClazz.addConstructor(defauleConstructor);

				try {
					for (CtConstructor c : ctConstructors) {
						tempC = CtNewConstructor.copy(c, newClazz, null);
						tempC.setBody("{super($$);}");
						newClazz.addConstructor(tempC);
					}
				} catch (DuplicateMemberException e) {
				}
				AbstractClassProcessingFactory.anthingToClass(newClazz, tempClass.getSuperClazz().getName(), isInitSystemClassLoader());
			} catch (Exception e) {
				//				e.printStackTrace();
				try {
					errorQueue.put(tempClass);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class worker extends RecursiveAction {

		private DefaultTempClass tempClass;

		private boolean assembly;

		/***
		 * 
		 * @param tempClass 建立的CCLASS
		 * @param assembly 是否需要组装
		 */
		public worker(DefaultTempClass tempClass, boolean assembly) {
			super();
			this.tempClass = tempClass;
			this.assembly = assembly;
		}

		@Override
		protected void compute() {
			//建立构造、构造加载
			try {
				CtConstructor tempC;
				CtClass newClazz = tempClass.getNewClazz();
				CtConstructor[] ctConstructors = tempClass.getSuperClazz().getDeclaredConstructors();
				CtConstructor defauleConstructor = CtNewConstructor.defaultConstructor(newClazz);
				defauleConstructor.setBody(tempClass.getConstructor());
				defauleConstructor.addCatch("", newClazz.getClassPool().get("java.lang.Exception"));
				//################构造###################
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
				AbstractClassProcessingFactory.anthingToClass(newClazz, tempClass.getSuperClazz().getName(), initSystemClassLoader);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					errorQueue.put(tempClass);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public boolean isInitSystemClassLoader() {
		return initSystemClassLoader;
	}

	public CreateClassFactory setInitSystemClassLoader(boolean initSystemClassLoader) {
		this.initSystemClassLoader = initSystemClassLoader;
		return this;
	}

}
