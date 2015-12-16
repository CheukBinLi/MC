package com.ben.mc.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class DefaultPool implements Pool<Object> {

	public static final String NORNAL_BEAN = "NORNAL_BEAN";

	private static final Map<Object, Object> CACHE = new ConcurrentHashMap<Object, Object>();

	private static final DefaultPool newInstance = new DefaultPool();

	protected DefaultPool() {
	}

	public static final DefaultPool newInstance() {
		return newInstance;
	}

	public Object put(Object key, Object value) {
		//		Object o = key;
		return CACHE.put(key, value);
	}

	public <T> T get(Object key) {
		//		Object x = CACHE;
		return (T) CACHE.get(key);
	}

	public Object remove(Object key) {
		return CACHE.remove(key);
	}

	public boolean containsKey(Object key) {
		return CACHE.containsKey(key);
	}

}
