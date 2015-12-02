package com.ben.mc.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings(value = { "unchecked", "rawtypes" })
public abstract class AbstractCachePoolFactory implements CachePoolFactory {

	public abstract Pool<Object> getCache();

	public AbstractCachePoolFactory() {
		super();
	}

	public <T> T get(Object o) {
		return (T) getCache().get(o);
	}

	public Object put(Object o, Object value) {
		return getCache().put(o, value);
	}

	public Object putList(Object o, Object... values) {
		return getCache().put(o, Arrays.asList(values));
	}

	public <T> T getNFloor(Object key, Integer... floor) throws Exception {
		List<Object> temp;
		int size = floor.length;
		temp = (List<Object>) getCache().get(key);
		for (int i = 0; i < size - 1; i++) {
			temp = (List<Object>) temp.get(floor[i]);
		}

		return (T) temp.get(floor[size - 1]);
	}

	public Integer[] addNFloor(boolean isConcurrent, Object key, Object value, Integer... floor) throws Exception {
		Object obj = getCache().get(key);
		List<Object> temp = null;
		if (null == obj) {
			if (isConcurrent)
				temp = new CopyOnWriteArrayList<Object>();
			else
				temp = new ArrayList<Object>();
			getCache().put(key, temp);
		}
		else
			temp = (List<Object>) obj;
		obj = null;
		int size = floor.length;
		List<Integer> path;
		if (isConcurrent)
			path = new CopyOnWriteArrayList<Integer>();
		else
			path = new ArrayList<Integer>();
		for (int i = 0; i < size - 1; i++) {
			if (floor[i] < temp.size()) {
				obj = temp.get(floor[i]);
				if (null == obj) {
					temp.set(floor[i], obj = new ArrayList<Object>());
				}
				path.add(floor[i]);
				temp = (List<Object>) obj;
			}
			else {
				if (isConcurrent)
					obj = new CopyOnWriteArrayList<Object>();
				else
					obj = new ArrayList<Object>();
				temp.add(obj);
				path.add(temp.size() - 1);
				temp = (List<Object>) obj;
			}
		}
		if (floor[size - 1] < temp.size()) {
			temp.set(floor[size - 1], value);
			path.add(floor[size - 1]);
		}
		else {
			temp.add(value);
			path.add(temp.size() - 1);
		}
		return path.toArray(new Integer[0]);
	}

	public <T> T get4Map(Object... key) {
		Object result = get(key[0]);
		if (null == result)
			return null;
		for (int i = 1; i < key.length; i++) {
			if (null == result)
				return null;
			try {
				result = ((Map<Object, Object>) result).get(key[i]);
			} catch (Exception e) {
				System.err.println("层次节目转换失败：key:" + Arrays.asList(key) + "出错节目:" + key[i - 1] + " 节目值:" + result);
				e.printStackTrace();
				return null;
			}
		}
		return (T) result;
	}

	public Object addNFloop4Map(boolean isConcurrent, Object value, Object... key) {
		Object obj = get(key[0]);
		Map container = null;
		Object node;
		if (null == obj) {
			if (isConcurrent)
				obj = new ConcurrentHashMap<Object, Object>();
			else
				obj = new HashMap<Object, Object>();
			put(key[0], obj);
		}
		container = (Map) obj;
		for (int i = 1; i < key.length - 1; i++) {
			node = container.get(key[i]);
			if (null == node) {
				if (isConcurrent)
					node = new ConcurrentHashMap<Object, Object>();
				else
					node = new HashMap<Object, Object>();
				container.put(key[i], node);
			}
			container = (Map) node;
		}
		return container.put(key[key.length - 1], value);
	}
}
