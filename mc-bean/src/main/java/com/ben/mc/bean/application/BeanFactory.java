package com.ben.mc.bean.application;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.ben.mc.bean.classprocessing.ClassInfo;
import com.ben.mc.bean.classprocessing.ClassProcessingFactory;
import com.ben.mc.bean.classprocessing.DefaultClassProcessingFactory;
import com.ben.mc.bean.util.ShortNameUtil;
import com.ben.mc.cache.CachePoolFactory;
import com.ben.mc.cache.DefaultCachePoolFactory;

public class BeanFactory {

	private static CachePoolFactory cachePoolFactory = new DefaultCachePoolFactory();

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static <T> T getBean(String name) throws InstantiationException, IllegalAccessException {
		//		System.out.println(name);
		Object A;
		A = cachePoolFactory.get4Map(ClassProcessingFactory.FULL_NAME_BEAN, name);
		if (null == A)
			A = cachePoolFactory.get4Map(ClassProcessingFactory.NICK_NAME_BEAN, ShortNameUtil.objectHumpNameLower(name));
		if (null == A)
			A = cachePoolFactory.get4Map(ClassProcessingFactory.SHORT_NAME_BEAN, name);
		if (null == A)
			return null;
		return (T) ((Class) A).newInstance();
	}

	/***
	 * 
	 * @param value
	 * @param key
	 * @return NULL新添加、Object复盖
	 */
	public static Object addBean(@SuppressWarnings("rawtypes") Class value, Object... key) {
		return cachePoolFactory.addNFloop4Map(true, value, key);
	}

	/***
	 * 
	 * @param value
	 * @param key
	 * @return NULL新添加、Object复盖
	 */
	public static Object addCache(Object value, Object... key) {
		return cachePoolFactory.addNFloop4Map(true, value, key);
	}

	/***
	 * 
	 * @param value
	 * @param key
	 * @return NULL新添加、Object复盖
	 */
	public static Object getCache(Object key) {
		return cachePoolFactory.get(key);
	}

	/***
	 * sortName find fullName
	 * 
	 * @param name
	 *            sortName
	 * @return
	 */
	public static String getFullName(String shortName) {
		return cachePoolFactory.get4Map(ClassProcessingFactory.SHORT_NAME_CACHE, shortName);
	}

	public static ClassInfo getClassInfo(String className) {
		return cachePoolFactory.get4Map(DefaultClassProcessingFactory.CLASS_INFO_CACHE, className);
	}

	public static Field getClassInfoField(String className, String fieldName) {
		ClassInfo ci = cachePoolFactory.get4Map(DefaultClassProcessingFactory.CLASS_INFO_CACHE, className);
		if (null == ci)
			return null;
		return ci.getFields().get(fieldName);
	}

	public static Method getClassInfoMethod(String className, String methodName) {
		ClassInfo ci = cachePoolFactory.get4Map(DefaultClassProcessingFactory.CLASS_INFO_CACHE, className);
		if (null == ci)
			return null;
		return ci.getMethods().get(methodName);
	}

	public static Object addClassInfo(ClassInfo classInfo) {
		return cachePoolFactory.addNFloop4Map(true, classInfo, DefaultClassProcessingFactory.CLASS_INFO_CACHE, classInfo.getName());
	}

}
