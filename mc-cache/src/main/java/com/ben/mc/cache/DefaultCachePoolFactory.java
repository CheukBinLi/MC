package com.ben.mc.cache;

public class DefaultCachePoolFactory extends AbstractCachePoolFactory {

	private static final DefaultCachePoolFactory newInstance = new DefaultCachePoolFactory(true);//并发模式

	public static final DefaultCachePoolFactory newInstance() {
		return newInstance;
	}

	private DefaultCachePoolFactory(boolean isConcurrent) {
		super(isConcurrent);
	}

	@Override
	public Pool<Object> getCache() {
		return DefaultPool.newInstance();
	}

}
