package com.ben.mc.classprocessing;

import com.ben.mc.cache.DefaultCachePoolFactory;

public class BeanFactory {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T getBean(String name) throws InstantiationException, IllegalAccessException {
		Class A;
		A = DefaultCachePoolFactory.newInstance().get4Map(ClassProcessingFactory.NICK_NAME_BEAN, name);
		if (null == A)
			A = DefaultCachePoolFactory.newInstance().get4Map(ClassProcessingFactory.FULL_NAME_BEAN, name);
		if (null == A)
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
		return DefaultCachePoolFactory.newInstance().addNFloop4Map(value, key);
	}

	/***
	 * 
	 * @param value
	 * @param key
	 * @return NULL新添加、Object复盖
	 */
	public static Object addCache(Object value, Object... key) {
		return DefaultCachePoolFactory.newInstance().addNFloop4Map(value, key);
	}

	/***
	 * sortName find fullName
	 * @param name sortName
	 * @return
	 */
	public static String getFullName(String shortName) {
		return DefaultCachePoolFactory.newInstance().get4Map(ClassProcessingFactory.SHORT_NAME_CACHE, shortName);
	}
}
