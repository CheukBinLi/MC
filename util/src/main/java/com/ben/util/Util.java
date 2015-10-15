package com.ben.util;

import java.util.ArrayList;

import javassist.CtClass;

public class Util {

	public static final ArrayList<String> getParameterTypes(CtClass... c) {
		ArrayList<String> a = new ArrayList<String>();
		for (CtClass b : c)
			a.add(b.getName());
		return a;
	}

	public static final ArrayList<String> getParameterTypes(Class... c) {
		ArrayList<String> a = new ArrayList<String>();
		for (Class b : c)
			a.add(b.getName());
		return a;
	}
}
