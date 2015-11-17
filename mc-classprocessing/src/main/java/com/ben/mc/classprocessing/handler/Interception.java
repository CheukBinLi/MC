package com.ben.mc.classprocessing.handler;

import java.lang.reflect.Method;

/***
 * 
 * Copyright 2015    ZHOU.BING.LI Individual All
 *  
 * ALL RIGHT RESERVED
 *  
 * CREATE ON 2015年11月17日 下午2:57:49
 *  
 * EMAIL:20796698@QQ.COM
 *  
 * GITHUB:https://github.com/fdisk123
 * 
 * @author ZHUO.BIN.LI
 * 
 * @see 拦截器接口
 *
 */
public interface Interception {

	/***
	 * 
	 * @param arg      this
	 * @param method   method
	 * @param params   参数
	 * @return 是否通过运行
	 */
	public boolean Intercept(Object arg, Method method, Object... params);

	/***
	 * 调用方法前运行
	 */
	public void before();

	/***
	 * 调用方法后运行
	 */
	public void after();

}
