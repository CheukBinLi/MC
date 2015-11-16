package com.ben.mc.classprocessing;

import com.ben.mc.cache.CachePoolFactory;
import com.ben.mc.cache.DefaultCachePoolFactory;

public class BeanFactory {

	private static CachePoolFactory cachePoolFactory = DefaultCachePoolFactory.newInstance();

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static <T> T getBean(String name) throws InstantiationException, IllegalAccessException {
		Class A;
		A = cachePoolFactory.get4Map(ClassProcessingFactory.NICK_NAME_BEAN, name);
		if (null == A)
			A = cachePoolFactory.get4Map(ClassProcessingFactory.FULL_NAME_BEAN, name);
		else if (null == A)
			A = cachePoolFactory.get4Map(ClassProcessingFactory.SHORT_NAME_BEAN, name);
		else if (null == A)
			return null;
		return (T) A.newInstance();
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
	 * sortName find fullName
	 * @param name sortName
	 * @return
	 */
	public static String getFullName(String shortName) {
		return cachePoolFactory.get4Map(ClassProcessingFactory.SHORT_NAME_CACHE, shortName);
	}

	public static ClassInfo getClassInfo(String className) {
		return cachePoolFactory.get4Map(DefaultClassProcessingFactory.CLASS_INFO_CACHE, className);
	}

	public static Object addClassInfo(String className) {
		return cachePoolFactory.addNFloop4Map(true, DefaultClassProcessingFactory.CLASS_INFO_CACHE, className);
	}

}
