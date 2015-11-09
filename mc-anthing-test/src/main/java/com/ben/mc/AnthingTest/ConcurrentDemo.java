package com.ben.mc.AnthingTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;

/**
 * Hello world!
 *
 */
public class ConcurrentDemo {

	static final Semaphore s2 = new Semaphore(0);

	public static void main(String[] args) {

		Thread t1 = new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.err.println("1");
			}
		});
		Thread t2 = new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.err.println("2");
			}
		});
		Thread t3 = new Thread(new Runnable() {

			public void run() {
				while (!Thread.interrupted()) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.err.println("3");
				}
			}
		});

		t1.start();
		t2.start();
		t3.start();

		ForkJoinPool forkJoinPool = new ForkJoinPool();
		//		forkJoinPool.

		ExecutorService e = new ForkJoinPool();

		Semaphore s = new Semaphore(1);

		Thread TS1 = new Thread(new A(s) {
			public void run() {
				try {
					this.s.acquire();
					Thread.sleep(3000);
					System.out.println("A1");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					this.s.release();
				}
			}
		});

		Thread TS2 = new Thread(new A(s) {
			public void run() {
				try {
					this.s.acquire();
					System.out.println("A2");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					s2.release();
					this.s.release();
				}
			}
		});
		Thread TS3 = new Thread(new A(s2) {
			public void run() {
				try {
					this.s.acquire();
					System.out.println("A3");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
				}
			}
		});

		TS1.start();
		TS2.start();
		TS3.start();

		final Phaser phaser = new Phaser(1);

		Thread TP1 = new Thread(new B(phaser, "A"));
		Thread TP2 = new Thread(new B(phaser, "B"));
		Thread TP3 = new Thread(new B(phaser, "C"));
		TP1.start();
		TP2.start();
		TP3.start();

		phaser.getPhase();
		phaser.arriveAndAwaitAdvance();
		System.out.println("完成第一阶段");
		phaser.getPhase();
		phaser.arriveAndAwaitAdvance();
		System.out.println("完成第二阶段");
		phaser.getPhase();
		phaser.arriveAndAwaitAdvance();
		System.out.println("完成第三阶段");

	}

	static abstract class A implements Runnable {
		Semaphore s;

		public A(Semaphore s) {
			super();
			this.s = s;
		}

		public Semaphore getS() {
			return s;
		}

		public void setS(Semaphore s) {
			this.s = s;
		}

	}

	static class B implements Runnable {

		Phaser phaser;
		String name;

		public Phaser getPhaser() {
			return phaser;
		}

		public void setPhaser(Phaser phaser) {
			this.phaser = phaser;
		}

		public B(Phaser phaser, String name) {
			super();
			this.name = name;
			this.phaser = phaser;
			this.phaser.register();
		}

		public void run() {

			this.phaser.arriveAndAwaitAdvance();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Thread:" + this.name + " this one!");
			this.phaser.arriveAndAwaitAdvance();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Thread:" + this.name + " this tow!");
			this.phaser.arriveAndAwaitAdvance();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Thread:" + this.name + " this three!");
			this.phaser.arriveAndDeregister();
		}

	}
}
