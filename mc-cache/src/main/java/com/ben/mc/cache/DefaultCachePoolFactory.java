package com.ben.mc.cache;

public class DefaultCachePoolFactory extends AbstractCachePoolFactory {

	private static final DefaultCachePoolFactory newInstance = new DefaultCachePoolFactory();//并发模式

	public static final DefaultCachePoolFactory newInstance() {
		return newInstance;
	}

	private DefaultCachePoolFactory() {
		super();
	}

	@Override
	public Pool<Object> getCache() {
		return DefaultPool.newInstance();
	}

}
