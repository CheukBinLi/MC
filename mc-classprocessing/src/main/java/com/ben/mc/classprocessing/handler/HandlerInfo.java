package com.ben.mc.classprocessing.handler;

/***
 * 
 * Copyright 2015 ZHOU.BING.LI Individual All
 * 
 * ALL RIGHT RESERVED
 * 
 * CREATE ON 2015年11月16日 下午4:30:52
 * 
 * EMAIL:20796698@QQ.COM
 * 
 * GITHUB:https://github.com/fdisk123
 * 
 * @author ZHUO.BIN.LI
 * 
 * @see 构造加载、注入/初始信息
 *
 */
public class HandlerInfo {

	private String x;

	private String[] imports;

	private Object newClazz;

	private Object additional;

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String[] getImports() {
		return imports;
	}

	public void setImports(String[] imports) {
		this.imports = imports;
	}

	public <T> T getAdditional() {
		return (T) additional;
	}

	public void setAdditional(Object additional) {
		this.additional = additional;
	}

	public <T> T getNewClazz() {
		return (T) newClazz;
	}

	public void setNewClazz(Object newClazz) {
		this.newClazz = newClazz;
	}

	public HandlerInfo(String x, Object newClazz, Object additional, String... imports) {
		super();
		this.x = x;
		this.imports = imports;
		this.additional = additional;
		this.newClazz = newClazz;
	}

	public HandlerInfo(String x, String... imports) {
		super();
		this.x = x;
		this.imports = imports;
	}

	public HandlerInfo() {
		super();
	}

}
