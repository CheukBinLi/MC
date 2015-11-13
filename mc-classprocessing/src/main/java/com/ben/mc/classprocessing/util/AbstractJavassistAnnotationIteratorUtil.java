package com.ben.mc.classprocessing.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

import com.ben.mc.cache.AbstractCachePoolFactory;
import com.ben.mc.cache.DefaultPool;
import com.ben.mc.cache.Pool;

public abstract class AbstractJavassistAnnotationIteratorUtil {

	public abstract Object getMember(Object o);

	public void b(CtClass clazz) {
		//		AnnotationsAttribute a = (AnnotationsAttribute) clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
		//		Annotation[] as = a.getAnnotations();
		//		Map<String, Object>
		//		Set set;
		//		Iterator it;
		//		for (Annotation an : as) {
		//			set = an.getMemberNames();
		//			it = set.iterator();
		//			while (it.hasNext()) {
		//				getMember(it.next());
		//			}
		//		}
	}
}
