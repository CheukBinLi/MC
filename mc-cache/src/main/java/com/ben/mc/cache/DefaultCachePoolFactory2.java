package com.ben.mc.cache;

public class DefaultCachePoolFactory2 extends AbstractCachePoolFactory {

	private static final DefaultCachePoolFactory2 newInstance = new DefaultCachePoolFactory2();

	public static final DefaultCachePoolFactory2 newInstance() {
		return newInstance;
	}

	@Override
	public Pool<Object> getCache() {
		return DefaultPool.newInstance();
	}

}
