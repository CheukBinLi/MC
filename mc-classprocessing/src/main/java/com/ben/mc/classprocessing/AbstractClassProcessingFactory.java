package com.ben.mc.classprocessing;

public abstract class AbstractClassProcessingFactory {

	public abstract ClazzInfo classProcessing(Class clazz) throws Throwable;

	public abstract ClazzInfo classProcessing(String clazz) throws Throwable;

}
