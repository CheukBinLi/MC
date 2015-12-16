package com.ben.mc.bean.classprocessing;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

	public void create(final CreateClassInfo classInfo, final boolean initSystemClassLoader) throws InterruptedException {
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
				errorQueue.put(tempClass);
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
