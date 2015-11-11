package com.ben.mc.util;

import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceFatory {
	static int MAX_QUEUE = 15;
	static int QUEUE = 5;
	static int mix = 2;
	public final static ExecutorService SINGLE_EXECUTOR_SERVICE = Executors.newCachedThreadPool();
	static final BlockingQueue<ExecutorService> EXECUTOR_SERVICES = new LinkedBlockingDeque<ExecutorService>();
	static final BlockingDeque<Integer> SUPPLEMENT = new LinkedBlockingDeque<Integer>();
	static final BlockingDeque<ExecutorService> RUNING_POOL = new LinkedBlockingDeque<ExecutorService>();

	static {
		if (EXECUTOR_SERVICES.size() < mix)
			SUPPLEMENT.add(QUEUE);
		SINGLE_EXECUTOR_SERVICE.execute(new Runnable() {
			public void run() {
				Integer count = 0;
				while (!Thread.interrupted()) {
					try {
						count = SUPPLEMENT.takeFirst();
						for (int i = 0; i < count; i++) {
							//EXECUTOR_SERVICES.add(Executors.newCachedThreadPool());
							EXECUTOR_SERVICES.add(new ThreadPoolExectorServiceX());
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				SINGLE_EXECUTOR_SERVICE.shutdown();
			}
		}));
	}

	public final static synchronized ExecutorService getSingleExector() {
		return SINGLE_EXECUTOR_SERVICE;
	}

	public final static synchronized ExecutorService getExector(int size) {
		ExecutorService t;
		try {
			if (size > 0)
				t = Executors.newFixedThreadPool(size);
			else
				t = EXECUTOR_SERVICES.take();
			RUNING_POOL.add(t);
			return t;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (EXECUTOR_SERVICES.size() < mix) {
				SUPPLEMENT.add(QUEUE - EXECUTOR_SERVICES.size());
			}
		}
	}

	public final static synchronized ExecutorService getExector() {
		return getExector(0);
	}

	static class ThreadPoolExectorServiceX extends ThreadPoolExecutor {

		public ThreadPoolExectorServiceX() {
			super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		}

		public ThreadPoolExectorServiceX(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

		}

		public ThreadPoolExectorServiceX(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);

		}

		public ThreadPoolExectorServiceX(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

		}

		public ThreadPoolExectorServiceX(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
		}

		@Override
		public void shutdown() {
			RUNING_POOL.remove(this);
			super.shutdown();
		}

		@Override
		public List<Runnable> shutdownNow() {
			RUNING_POOL.remove(this);
			return super.shutdownNow();
		}
	}

	public static void main(String[] args) {
		ExecutorService s = getExector();
		s.submit(new Runnable() {

			public void run() {
				System.out.println("e:" + EXECUTOR_SERVICES.size());
				System.out.println("s:" + SUPPLEMENT.size());
				System.out.println("r:" + RUNING_POOL.size());
				System.out.println("x");
			}
		});
		s.shutdown();
		System.err.println("e:" + EXECUTOR_SERVICES.size());
		System.err.println("s:" + SUPPLEMENT.size());
		System.err.println("r:" + RUNING_POOL.size());

	}
}
