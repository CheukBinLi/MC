package com.ben.cache;


public class DefaultCachePoolFactory extends AbstaractCachePoolFactory {

	private static final DefaultCachePoolFactory newInstance = new DefaultCachePoolFactory();

	public static final DefaultCachePoolFactory newInstance() {
		return newInstance;
	}

	private DefaultCachePoolFactory() {
	}

	@Override
	public Pool<Object> getCache() {
		return DefaultPool.newInstance();
	}

}
