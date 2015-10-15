package com.ben.cache;

public interface Pool<K> {

	public Object put(K key, Object value);

	public <T> T get(K key);

	public Object remove(K key);

	public boolean containsKey(K key);

}
