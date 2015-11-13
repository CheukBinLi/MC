package com.ben.mc.classprocessing;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import com.ben.mc.annotation.Register;
import com.ben.mc.scan.Scan;
import com.ben.mc.util.Util;

import create.old;

public class DefaultClassProcessing extends DefaultClassProcessingFactory {

	@Override
	public ClazzInfo classProcessing(Class clazzz) throws NotFoundException, CannotCompileException {
		return classProcessing(clazzz.getCanonicalName());
	}

	@Override
	public ClazzInfo classProcessing(String clazzName) throws NotFoundException, CannotCompileException {
		ClassPool classPool = ClassPool.getDefault();
		CtClass cz = classPool.get(clazzName);
		classPool.importPackage(clazzName);//继承
		classPool.importPackage("java.lang.reflect.Method");//添加反射引用

		CtClass newClass = classPool.makeClass(clazzName + Impl);//新建代理类
		newClass.setSuperclass(cz);//继承

		//构造块
		CtConstructor tempC;
		CtConstructor[] ctConstructors = cz.getDeclaredConstructors();
		newClass.addConstructor(CtNewConstructor.defaultConstructor(newClass));
		for (CtConstructor c : ctConstructors) {
			try {
				tempC = CtNewConstructor.copy(c, newClass, null);
				tempC.setBody("{super($$);}");
				newClass.addConstructor(tempC);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}

		//字段块
		//		CtField[] ctFields = cz.getDeclaredFields();
		//		for (CtField f : ctFields)
		//			System.out.println(f.getFieldInfo().getConstantValue());

		//方法块
		CtMethod[] ctMethods = cz.getDeclaredMethods();
		CtMethod tempM;//复制方法名
		Map<String, Method> tempMethod = new HashMap<String, Method>();
		for (CtMethod m : ctMethods) {
			tempMethod.put(String.format("%s %s %s(%s);", Modifier.toString(m.getModifiers()), m.getReturnType().getName(), m.getName(), Util.getParameterTypes(m.getParameterTypes())), null);
			//System.err.println(String.format("%s %s %s(%s);", Modifier.toString(m.getModifiers()), m.getReturnType().getName(), m.getName(), Util.getParameterTypes(m.getParameterTypes())));
			tempM = CtNewMethod.copy(m, newClass, null);
			if ("void".equals(tempM.getReturnType().getName()))
				tempM.setBody("{super." + tempM.getName() + "($$);}");
			else
				tempM.setBody("{ return super." + tempM.getName() + "($$);}");
			//				CtNewMethod.make(src, declaring, delegateObj, delegateMethod)
			//方法修改
			//			if (m.getName().equals("x")) {
			//				//tempM.setBody("{$proceed($$);}", "this", "mba");
			//				//tempM.setBody("{n nn = new n();" + "Method a = n.class.getDeclaredMethod(\"a\", new Class[] { Integer.TYPE });" + "a.invoke(nn, new Object[] { Integer.valueOf(1) });}");
			//				tempM.setBody("{Method a = n.class.getDeclaredMethod(\"axx\", new Class[] { Integer.TYPE });}");
			//			}
			newClass.addMethod(tempM);
		}

		//测试输出
		try {
			newClass.writeFile("D:/Desktop");
		} catch (IOException e) {
			e.printStackTrace();
		}

		//		Class clazz = newClass.toClass();
		//		System.out.println(clazz.getCanonicalName());
		//		DefaultCachePoolFactory.newInstance().addNFloop4Map(new ClazzInfo(clazz, tempMethod), DefaultPool.NORNAL_BEAN, clazz.getCanonicalName());
		//		return clazz;
		return new ClazzInfo(newClass.toClass(), tempMethod);
	}

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, NotFoundException, CannotCompileException {
		//		1.0
		old o = (old) ((ClazzInfo) new DefaultClassProcessing().classProcessing(old.class)).getClazz().newInstance();
		o.setFX("mba");
		System.out.println(o.getFX());
		o.x();
		//2.0

		//		Map<String, CtClass>result=Scan.doScan("com.");

	}

	public void first(Set<String> classNames) throws NotFoundException, ClassNotFoundException {
		//扫描注册
		ClassPool classPool = ClassPool.getDefault();
		String tempClassName = null;
		Class register = Register.class;
		CtClass clazz;
		Object isRegister;
		Iterator<String> it = classNames.iterator();
		while (it.hasNext()) {
			tempClassName = it.next();
			clazz = classPool.get(tempClassName);
			isRegister = clazz.getAnnotation(register);

			AnnotationsAttribute a = (AnnotationsAttribute) clazz.getClassFile2().getAttribute(AnnotationsAttribute.visibleTag);
			Annotation[] as = a.getAnnotations();
			for (Annotation an : as) {
				//				System.out.println(String.format("MemberNames:%s ", an.getMemberNames()));
				Set anSet = an.getMemberNames();
				Iterator anit = anSet.iterator();
				while (anit.hasNext()) {
					System.out.println("type:" + anit.next().getClass());
				}
				//			AnnotationsAttribute a = (AnnotationsAttribute) clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
				//				for()
			}
			System.err.println(a);
			System.err.println(a);
			//			System.err.println(a.getName());

			if (null != isRegister)
				System.out.println(isRegister);
		}
	}
}
