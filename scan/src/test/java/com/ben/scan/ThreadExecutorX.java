package com.ben.scan;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutorX {

	public static void xxxxx() {
		ExecutorService e = Executors.newCachedThreadPool();
		e.submit(new Runnable() {
			public void run() {
				System.out.println("HI");
			}
		});
	}

	public static void main(String[] args) {
		xxxxx();
	}
}
