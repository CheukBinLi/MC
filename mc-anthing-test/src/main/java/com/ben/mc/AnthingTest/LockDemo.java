package com.ben.mc.AnthingTest;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockDemo {

	public static void main(String[] args) {

		ReentrantLock lock = new ReentrantLock();

		//		ExecutorService executorService = new ForkJoinPool();
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(new A<Object>(lock) {
			public Object call() throws Exception {
				try {
					this.lock.lock();
					//					if (this.lock.lock()) {
					System.err.println(" This thread sleep 10 second！ ");
					Thread.sleep(10000);
					System.err.println(" This thread wake up！ ");
					return true;
					//					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					this.lock.unlock();
				}
				return false;
			}
		});
		executorService.submit(new A<Object>(lock) {
			public Object call() throws Exception {
				try {
					System.out.println(" Second Thread wait for lock. ");
					this.lock.lock();
					System.out.println(" Running finish!");
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					this.lock.unlock();
				}
				return false;
			}
		});

		executorService.shutdown();

	}

	static abstract class A<T> implements Callable<T> {
		Lock lock;

		public A(Lock lock) {
			super();
			this.lock = lock;
		}

	}

}
