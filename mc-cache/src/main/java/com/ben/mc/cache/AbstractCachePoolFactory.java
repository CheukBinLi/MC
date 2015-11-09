package com.ben.mc.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCachePoolFactory implements CachePoolFactory {

	public abstract Pool<Object> getCache();

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

	public Integer[] addNFloor(Object key, Object value, Integer... floor) throws Exception {
		Object obj = getCache().get(key);
		List<Object> temp = null;
		if (null == obj) {
			temp = new ArrayList<Object>();
			getCache().put(key, temp);
		}
		else
			temp = (List<Object>) obj;
		obj = null;
		List<Object> X = null;
		int size = floor.length;
		List<Integer> path = new ArrayList<Integer>();
		for (int i = 0; i < size - 1; i++) {
			//长度
			if (floor[i] < temp.size()) {
				obj = temp.get(floor[i]);
				if (null == obj) {
					temp.set(floor[i], obj = new ArrayList<Object>());
				}
				path.add(floor[i]);
				temp = (List<Object>) obj;
			}
			//大于
			else {
				temp.add(obj = new ArrayList<Object>());
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
				// e.printStackTrace();
				return null;
			}
		}
		return (T) result;
	}

	public void addNFloop4Map(Object value, Object... key) {
		Object obj = get(key[0]);// 第一节
		Map container = null;
		Object node;// 第n个节点
		if (null == obj) {
			obj = new HashMap<Object, Object>();
			put(key[0], obj);// 添加第一节
		}
		container = (Map) obj;
		for (int i = 1; i < key.length - 1; i++) {
			// System.out.println("put:" + key[i]);
			node = container.get(key[i]);
			if (null == node) {
				node = new HashMap<Object, Object>();
				container.put(key[i], node);
			}
			container = (Map) node;// 下一节
		}
		//		System.out.println("key:" + key[key.length - 1]+"   "+value);
		container.put(key[key.length - 1], value);
		String a="";
	}

	public static void main(String[] args) throws Exception {
		ArrayList<Integer> ax = new ArrayList<Integer>();
		ax.add(99);
		ax.set(0, 44);
		System.out.println(ax.get(0));
		AbstractCachePoolFactory a = DefaultCachePoolFactory.newInstance();
		a.putList("小明", "a", 1, 2, 3, 4, 5, 6, 7, "小B");
		System.out.println(a.get("小明"));
		Integer[] A1 = a.addNFloor("学校分级", "野鸡小学", 1);
		Integer[] A2 = a.addNFloor("学校分级", "野鸡小学1-1班", 1, 1);
		Integer[] A3 = a.addNFloor("学校分级", "野鸡小学1-2班", 1, 1);
		Integer[] A33 = a.addNFloor("学校分级", "野鸡小学1-3班", 1, 7, 1, 1, 1);
		Integer[] A4 = a.addNFloor("学校分级", "野鸡小学2", 2);
		System.out.println(a.getNFloor("学校分级", A1));
		System.out.println(a.getNFloor("学校分级", A2));
		System.out.println(a.getNFloor("学校分级", A3));
		System.out.println(a.getNFloor("学校分级", A33));
		System.out.println(a.getNFloor("学校分级", A4));
		Object o = a.get("学校分级");
		System.out.println(a.get("学校分级"));

		///
		a.addNFloop4Map("小学鸡-1-1", "学校", "垃圾学校", "4年一班");
		a.addNFloop4Map("小学鸡-1-2", "学校", "垃圾学校", "4年二班");
		a.addNFloop4Map("小学鸡-1-3", "学校", "垃圾学校", "4年三班");
		a.addNFloop4Map("小学鸡-1-4", "学校", "垃圾学校", "4年一班");
		System.out.println(a.get4Map("学校", "垃圾学校", "4年一班"));
	}
}
