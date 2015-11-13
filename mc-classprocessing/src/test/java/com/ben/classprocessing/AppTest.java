package com.ben.classprocessing;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.orm.hibernate4.testAA.xxxx;

import javassist.NotFoundException;

import com.ben.mc.classprocessing.DefaultClassProcessing;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	public void firstTest() {
		Set<String> clazz = new HashSet<String>();
		clazz.add(xxxx.class.getCanonicalName());
		System.err.println(xxxx.class.getCanonicalName());
		try {
			new DefaultClassProcessing().first(clazz);
		} catch (ClassNotFoundException | NotFoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		Set<String> clazz = new HashSet<String>();
		System.err.println(xxx.class.getCanonicalName());
		clazz.add(xxx.class.getCanonicalName());
		try {
			new DefaultClassProcessing().first(clazz);
		} catch (ClassNotFoundException | NotFoundException e) {
			e.printStackTrace();
		}
	}
}
