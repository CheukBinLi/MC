package com.ben.mc.cache;

public class DefaultCachePoolFactory extends AbstractCachePoolFactory {

	private static final DefaultCachePoolFactory newInstance = new DefaultCachePoolFactory();

//	public static final DefaultCachePoolFactory newInstance() {
//		return newInstance;
//	}

	@Override
	public Pool<Object> getCache() {
		return DefaultPool.newInstance();
	}

}
