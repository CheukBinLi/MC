package com.ben.cache;

public interface CachePoolFactory {

	Object getCache();

	public <T> T get(Object o);

	public <T> T get4Map(Object... o);

	/***
	 * 慎用
	 * @param value
	 * @param floorKey 单数为KEY,双数为层位置
	 */
	public <T> T getNFloor(Object key, Integer... floor) throws Exception;

	/***
	 * 
	 * @param o
	 * @param t
	 * @return 不存在返回null,存在返回被替换对象
	 */
	public Object put(Object o, Object value);

	public Object putList(Object o, Object... values);

	public void addNFloop4Map(Object value, Object... key);

	/***
	 * 
	 * 慎用
	 * @param value
	 * @param floorKey 单数为KEY,双数为层位置
	 * @return 返回实际楼层
	 * @throws Exception
	 */
	public Integer[] addNFloor(Object Key, Object value, Integer... floor) throws Exception;

}
