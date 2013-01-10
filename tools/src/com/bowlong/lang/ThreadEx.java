package com.bowlong.lang;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.bowlong.util.DateEx;
import com.bowlong.util.NewDate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ThreadEx {
	private static MyThreadFactory getThreadFactory(String name) {
		return new MyThreadFactory(name, true);
	}

	// 创建一个无限线程池
	static ThreadFactory cachedFactory;

	public static ExecutorService newCachedThreadPool() {
		if (cachedFactory == null) {
			cachedFactory = getThreadFactory("CachedPool");
		}
		ExecutorService unboundedPool = Executors.newCachedThreadPool(cachedFactory);
		return unboundedPool;
	}

	// 创建一个固定大小线程
	static ThreadFactory fixedFactory;

	public static ExecutorService newFixedThreadPool(int nThreads) {
		if (fixedFactory == null) {
			fixedFactory = getThreadFactory("FixedPool");
		}
		ExecutorService fixPool = Executors.newFixedThreadPool(nThreads, fixedFactory);
		return fixPool;
	}

	// 单线程
	static ThreadFactory singleFactory;

	public static ExecutorService newSingleThreadExecutor() {
		if (singleFactory == null) {
			singleFactory = getThreadFactory("SinglePool");
		}
		ExecutorService singlePool = Executors.newSingleThreadExecutor(singleFactory);
		return singlePool;
	}

	// 用线程池执行任务
	public static void execute(Executor executor, Runnable r) {
		executor.execute(r);
	}

	// /////////////////////////////////////////////////////////////////
	// 用线程池执行
	static Executor _executor = null;

	public static void execute(Runnable r) {
		if (_executor == null)
			_executor = newCachedThreadPool();

		execute(_executor, r);
	}

	static Executor _singleExecutor = null;

	public static void executeSingle(Runnable r) {
		if (_singleExecutor == null)
			_singleExecutor = newSingleThreadExecutor();

		execute(_singleExecutor, r);
	}

	static ThreadFactory scheduledFactory;

	public static ScheduledExecutorService newScheduledPool(int nThreads) {
		if (scheduledFactory == null) {
			scheduledFactory = getThreadFactory("ScheduledPool");
		}
		return Executors.newScheduledThreadPool(nThreads, scheduledFactory);
	}

	// /////////////////////////////////////////////////////////////////
	// 使用调度器执行
	static ScheduledExecutorService _scheduledPool = null;

	// 定时执行
	public static ScheduledFuture<?> schedule(ScheduledExecutorService scheduledPool, Runnable r, long delay, TimeUnit unit) {
		return scheduledPool.schedule(r, delay, unit);
	}

	public static ScheduledFuture<?> schedule(ScheduledExecutorService scheduledPool, Callable r, long delay, TimeUnit unit) {
		return scheduledPool.schedule(r, delay, unit);
	}

	public static ScheduledFuture<?> schedule(ScheduledExecutorService scheduledPool, Runnable r, long delay) {
		return scheduledPool.schedule(r, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> schedule(Runnable r, long delay) {
		return schedule(r, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> schedule(Runnable r, long delay, TimeUnit unit) {
		if (_scheduledPool == null)
			_scheduledPool = newScheduledPool(32);

		return schedule(_scheduledPool, r, delay, unit);
	}

	public static ScheduledFuture<?> schedule(Callable r, long delay) {
		return schedule(r, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> schedule(Callable r, long delay, TimeUnit unit) {
		if (_scheduledPool == null)
			_scheduledPool = newScheduledPool(32);

		return schedule(_scheduledPool, r, delay, unit);
	}

	// 定时执行,再间隔调度重复执行
	public static ScheduledFuture<?> scheduleWithFixedDelay(ScheduledExecutorService scheduledPool, Runnable r, long initialDelay, long delay, TimeUnit unit) {
		return scheduledPool.scheduleWithFixedDelay(r, initialDelay, delay, unit);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(ScheduledExecutorService scheduledPool, Runnable r, long initialDelay, long delay) {
		return scheduledPool.scheduleWithFixedDelay(r, initialDelay, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable r, long initialDelay, long delay) {
		return scheduleWithFixedDelay(r, initialDelay, delay, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable r, long initialDelay, long delay, TimeUnit unit) {
		if (_scheduledPool == null)
			_scheduledPool = newScheduledPool(32);

		return scheduleWithFixedDelay(_scheduledPool, r, initialDelay, delay, unit);
	}

	public static final long now() {
		return System.currentTimeMillis();
	}

	// 定时执行
	public static final ScheduledFuture<?> scheduled1(Runnable r, Date d) {
		if (_scheduledPool == null)
			_scheduledPool = Executors.newScheduledThreadPool(1);
		long delay = d.getTime() - now();
		delay = delay <= 0 ? 1 : delay;
		return _scheduledPool.schedule(r, delay, TimeUnit.MILLISECONDS);
	}

	// 确定时分秒，每日执行
	public static final ScheduledFuture<?> scheduled64EveryDay(Runnable r, int hour, int minute, int sec) {
		if (_scheduledPool == null)
			_scheduledPool = Executors.newScheduledThreadPool(32);

		NewDate dat = new NewDate();
		long now = now();
		long h = dat.getHour();
		long m = dat.getMinute();
		long s = dat.getSecound();
		long tomorrow = dat.addDay(-1).setHourMinuteSec(0, 0, 0).getTime();
		long initialDelay = 0;
		long e1 = h * DateEx.TIME_HOUR + m * DateEx.TIME_MINUTE + s * DateEx.TIME_SECOND;
		long e2 = hour * DateEx.TIME_HOUR + minute * DateEx.TIME_MINUTE + sec * DateEx.TIME_SECOND;
		if (e1 < e2) {
			initialDelay = e2 - e1;
		} else {
			initialDelay = tomorrow - now + e2;
		}
		long delay = DateEx.TIME_DAY;
		return _scheduledPool.scheduleAtFixedRate(r, initialDelay, delay, TimeUnit.MILLISECONDS);

	}

	// /////////////////////////////////////////////////////////////////
	public static void Sleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /////////////////////////////////////////////////////////////////

	// private class T implements Runnable {
	// String s;
	//
	// public T(String s) {
	// this.s = s;
	// }
	//
	// public void run() {
	// System.out.println(s + ":" + now());
	// }
	// }
	//
	// public void test() {
	// // 定时执行
	// ScheduledFuture f = schedule(new T("s"), 1000);
	// System.out.println(f.isDone());
	// // 定时间隔执行
	// ScheduledFuture f2 = scheduleWithFixedDelay(new T("swfd"), 1000, 1000);
	// Sleep(5000);
	// System.out.println(f.isDone());
	// f2.cancel(true);
	// Sleep(5000);
	// }
	//
	// public static void main(String[] args) {
	// ThreadEx t = new ThreadEx();
	// t.test();
	// }
}
