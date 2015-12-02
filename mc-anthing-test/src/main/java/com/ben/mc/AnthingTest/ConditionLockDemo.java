package com.ben.mc.AnthingTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionLockDemo {

	private Lock lock = new ReentrantLock();

	private Condition notFull = lock.newCondition();
	private Condition notEmpty = lock.newCondition();

	private transient Object[] o = new Object[2];
	private transient static int currentUse = 1;

	public Object take() throws InterruptedException {
		lock.lock();
		try {
			Object x;
			while ((x = getFirst()) == null) {
				System.err.println("take等待");
				notEmpty.await();
			}
			currentUse--;
			System.err.println("take完成");
			return x;
		} finally {
			lock.unlock();
		}
	}

	private Object getFirst() {
		notFull.signal();
		return o[0];
	}

	private Object addLast(Object obj) {
		o[currentUse - 1] = obj;
		currentUse++;
		notEmpty.signal();
		return o[0];
	}

	public void add(Object obj) throws InterruptedException {
		lock.lock();
		try {
			while (currentUse > o.length) {
				System.err.println("添加等待");
				notFull.await();
			}
			Thread.sleep(5000);
			System.err.println("添加完成");
			addLast(obj);
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws InterruptedException {
		final ConditionLockDemo cld = new ConditionLockDemo();
		Runnable add = new Runnable() {
			public void run() {
				try {
					cld.add(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Runnable less = new Runnable() {
			public void run() {
				try {
					System.err.println(cld.take());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		//		ForkJoinPool pool = new ForkJoinPool();
		ExecutorService pool = Executors.newCachedThreadPool();
		pool.execute(less);
		pool.execute(less);
		pool.execute(less);
		pool.execute(less);
		Thread.sleep(5000);
		pool.execute(add);
		pool.execute(add);
		pool.execute(add);
		pool.execute(add);

	}
}
