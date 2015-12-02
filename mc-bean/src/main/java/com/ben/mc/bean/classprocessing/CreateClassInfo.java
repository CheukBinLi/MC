package com.ben.mc.bean.classprocessing;

import java.util.ArrayList;
import java.util.List;

public class CreateClassInfo {

	private List<DefaultTempClass> firstQueue = new ArrayList<DefaultTempClass>();
	private List<DefaultTempClass> secondQueue = new ArrayList<DefaultTempClass>();

	public CreateClassInfo addAll(final CreateClassInfo clazzs) {
		firstQueue.addAll(clazzs.firstQueue);
		secondQueue.addAll(clazzs.secondQueue);
		return this;
	}

	public CreateClassInfo() {
		super();
	}

	public List<DefaultTempClass> getFirstQueue() {
		return firstQueue;
	}

	public CreateClassInfo addFirstQueue(DefaultTempClass firstQueue) {
		this.firstQueue.add(firstQueue);
		return this;
	}

	public List<DefaultTempClass> getSecondQueue() {
		return secondQueue;
	}

	public CreateClassInfo addSecondQueue(DefaultTempClass secondQueue) {
		this.secondQueue.add(secondQueue);
		return this;
	}

}
