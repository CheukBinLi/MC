package com.ben.mc.bean.classprocessing;

import javassist.CtClass;

public class DefaultTempClass implements TempClass {

	private CtClass superClazz;
	private CtClass newClazz;
	private String constructorBody;
	//	private List<CtField> fields;
	//	private List<CtMethod> mMethods;
	private Object additional;

	public DefaultTempClass() {
		super();
	}

	public DefaultTempClass(CtClass oldClazz, CtClass newClazz) {
		super();
		this.superClazz = oldClazz;
		this.newClazz = newClazz;
	}

	public DefaultTempClass(CtClass superClazz, CtClass newClazz, String constructorBody, Object additional) {
		super();
		this.superClazz = superClazz;
		this.newClazz = newClazz;
		this.constructorBody = constructorBody;
		this.additional = additional;
	}

	public CtClass getSuperClazz() {
		return superClazz;
	}

	public void setSuperClazz(CtClass superClazz) {
		this.superClazz = superClazz;
	}

	public CtClass getNewClazz() {
		return newClazz;
	}

	public void setNewClazz(CtClass newClazz) {
		this.newClazz = newClazz;
	}

	public String getConstructor() {
		return constructorBody;
	}

	public void setConstructor(String constructorBody) {
		this.constructorBody = constructorBody;
	}

	//	public List<CtField> getFields() {
	//		if (null == this.fields)
	//			this.fields = new ArrayList<CtField>();
	//		return fields;
	//	}
	//
	//	public void setFields(List<CtField> fields) {
	//		this.fields = fields;
	//	}
	//
	//	public List<CtMethod> getmMethods() {
	//		if (null == this.mMethods)
	//			this.mMethods = new ArrayList<CtMethod>();
	//		return mMethods;
	//	}
	//
	//	public void setmMethods(List<CtMethod> mMethods) {
	//		this.mMethods = mMethods;
	//	}

	public Object getAdditional() {
		return additional;
	}

	public void setAdditional(Object additional) {
		this.additional = additional;
	}

}
