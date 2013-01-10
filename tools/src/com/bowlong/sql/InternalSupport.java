package com.bowlong.sql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bowlong.objpool.StringBufPool;
import com.bowlong.util.DateEx;
import com.bowlong.util.FormatEx;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class InternalSupport {

	// NO_CACHE, // 不缓存
	// FULL_CACHE, // 全缓存
	// HOT_CACHE, // 热缓存 (未使用)
	// FULL_MEMORY, // 全内存
	// STATIC_CACHE // 静态缓存(数据不增长)

	public static final SimpleDateFormat sdf_yyyy_MM_dd = FormatEx
			.getFormat("yyyy-MM-dd");

	public static final long difference(int l1, int l2) {
		return l2 - l1;
	}

	public static final Date time() {
		return new java.util.Date();
	}

	public static final boolean isTimeout(long TIMEOUT, Date DLASTTIME) {
		long LASTTIME = DLASTTIME.getTime();
		return isTimeout(TIMEOUT, LASTTIME);
	}

	public static final List newList() {
		return Collections.synchronizedList(new ArrayList());
		// return new CopyOnWriteArrayList();
	}

	public static final Map newMap() {
		// return Collections.synchronizedMap(new HashMap());
		return new ConcurrentHashMap();
	}

	public static final Map newSortedMap() {
		return Collections.synchronizedMap(new TreeMap());
	}

	public static final Set newSet() {
		// return Collections.synchronizedSet(new HashSet());
		return new CopyOnWriteArraySet();
	}

	public static final Set newSortedSet() {
		return Collections.synchronizedSet(new TreeSet());
	}

	// public static final Map newMap(int size) {
	// return Collections.synchronizedMap(new HashMap(size));
	// // return new Hashtable();
	// }

	// ///////////////////////////////////////////////////////////////////////
	public static final int pageCount(int count, int pageSize) {
		int page = count / pageSize;

		page = count == page * pageSize ? page : page + 1;
		return page;
	}

	public static final List getPage(List v, int page, int pageSize) {
		int count = v.size();
		int begin = page * pageSize;
		int end = begin + pageSize;
		if (begin > count || begin < 0 || end < 0)
			return new Vector();
		end = count < end ? count : end;
		if (end <= begin)
			new Vector();
		return v.subList(begin, end);
	}

	// ///////////////////////////////////////////////////
	public static final boolean isTimeout(long LASTTIME, long TIMEOUT) {
		long l2 = System.currentTimeMillis();
		return isTimeout(l2, LASTTIME, TIMEOUT);
	}

	public static final boolean isTimeout(long TIME1, long TIME2, long TIMEOUT) {
		if (TIMEOUT <= 0)
			return false;
		long l2 = TIME1;
		long t = l2 - TIME2;
		return (t > TIMEOUT);
	}

	public static final boolean isTimeout(Date DLASTTIME, long TIMEOUT) {
		if (DLASTTIME == null)
			return false;
		long LASTTIME = DLASTTIME.getTime();
		return isTimeout(TIMEOUT, LASTTIME);
	}

	// ///////////////////////////////////////////////////
	// 数据库处理单线程池
	// private static Executor _singleExecutor = null;
	//
	// public synchronized static final void executeSingle(Runnable r) {
	// if (_singleExecutor == null)
	// _singleExecutor = Executors.newSingleThreadExecutor();
	// _singleExecutor.execute(r);
	// }
	//
	// // 4线程并发线程池
	// private static Executor _4FixedExecutor = null;
	//
	// public synchronized static final void execute4Fixed(Runnable r) {
	// if (_4FixedExecutor == null)
	// _4FixedExecutor = Executors.newFixedThreadPool(4);
	// _4FixedExecutor.execute(r);
	// }

	// 线程并发线程池
	private static ExecutorService _threadPool = null;

	public static final void execute(Runnable r) {
		if (_threadPool == null)
			_threadPool = Executors.newCachedThreadPool();

		_threadPool.execute(r);
	}

	public static final <T> Future<T> execute(Callable<T> r) {
		if (_threadPool == null)
			_threadPool = Executors.newCachedThreadPool();

		return _threadPool.submit(r);
	}

	// 错误堆栈的内容
	public static final String e2s(Exception e) {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append(e);
			sb.append("\r\n");
			for (StackTraceElement ste : e.getStackTrace()) {
				sb.append("at ");
				sb.append(ste);
				sb.append("\r\n");
			}
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final long now() {
		return System.currentTimeMillis();
	}

	public static final Date beginningToday2() {
		try {
			String s = beginningToday3();
			Date d = sdf_yyyy_MM_dd.parse(s);
			return d;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static final String beginningToday3() {
		try {

			return sdf_yyyy_MM_dd.format(new Date());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// 今天凌晨(0点0分)
	public static final long beginningToday() {
		try {
			Date d = beginningToday2();
			return d.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static final long beginningTomorrow() {
		try {
			return beginningToday() + DateEx.TIME_DAY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static final int compareTo(Object v1, Object v2) {

		if (v1 == null || v2 == null)
			return 0;

		if (v1 instanceof Boolean && v2 instanceof Boolean) {
			Boolean i1 = (Boolean) v1;
			Boolean i2 = (Boolean) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Byte && v2 instanceof Byte) {
			Byte i1 = (Byte) v1;
			Byte i2 = (Byte) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Short && v2 instanceof Short) {
			Short i1 = (Short) v1;
			Short i2 = (Short) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Integer && v2 instanceof Integer) {
			Integer i1 = (Integer) v1;
			Integer i2 = (Integer) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Long && v2 instanceof Long) {
			Long i1 = (Long) v1;
			Long i2 = (Long) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.math.BigInteger
				&& v2 instanceof java.math.BigInteger) {
			java.math.BigInteger i1 = (java.math.BigInteger) v1;
			java.math.BigInteger i2 = (java.math.BigInteger) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.math.BigDecimal
				&& v2 instanceof java.math.BigDecimal) {
			java.math.BigDecimal i1 = (java.math.BigDecimal) v1;
			java.math.BigDecimal i2 = (java.math.BigDecimal) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Float && v2 instanceof Float) {
			Float i1 = (Float) v1;
			Float i2 = (Float) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Double && v2 instanceof Double) {
			Double i1 = (Double) v1;
			Double i2 = (Double) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Date && v2 instanceof Date) {
			Date i1 = (Date) v1;
			Date i2 = (Date) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.sql.Date && v2 instanceof java.sql.Date) {
			java.sql.Date i1 = (java.sql.Date) v1;
			java.sql.Date i2 = (java.sql.Date) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.sql.Timestamp
				&& v2 instanceof java.sql.Timestamp) {
			java.sql.Timestamp i1 = (java.sql.Timestamp) v1;
			java.sql.Timestamp i2 = (java.sql.Timestamp) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.sql.Time && v2 instanceof java.sql.Time) {
			java.sql.Time i1 = (java.sql.Time) v1;
			java.sql.Time i2 = (java.sql.Time) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof String && v2 instanceof String) {
			String i1 = (String) v1;
			String i2 = (String) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.lang.Enum && v2 instanceof java.lang.Enum) {
			java.lang.Enum i1 = (java.lang.Enum) v1;
			java.lang.Enum i2 = (java.lang.Enum) v2;
			return i1.compareTo(i2);
		}
		return 0;
	}

	// public static ScheduledExecutorService scheduled1Pool = null;
	//
	// public synchronized final static ScheduledFuture<?> scheduled1(Runnable
	// r, long delay) {
	// if (scheduled1Pool == null)
	// scheduled1Pool = Executors.newScheduledThreadPool(1);
	// return scheduled1Pool.schedule(r, delay, TimeUnit.MILLISECONDS);
	// }
	//
	// // 定时执行
	// public synchronized final static ScheduledFuture<?> scheduled1(Runnable
	// r, Date d) {
	// if (scheduled1Pool == null)
	// scheduled1Pool = Executors.newScheduledThreadPool(1);
	// long delay = d.getTime() - now();
	// delay = delay <= 0 ? 1 : delay;
	// return scheduled1Pool.schedule(r, delay, TimeUnit.MILLISECONDS);
	// }
	//
	// public static ScheduledExecutorService scheduled64Pool = null;
	//
	// // 延时执行
	// public synchronized final static ScheduledFuture<?> scheduled64(Runnable
	// r, long delay) {
	// if (scheduled64Pool == null)
	// scheduled64Pool = Executors.newScheduledThreadPool(64);
	// return scheduled64Pool.schedule(r, delay, TimeUnit.MILLISECONDS);
	// }
	//
	// // 定间隔时间执行
	// public synchronized final static ScheduledFuture<?>
	// scheduled4FixedDelay(Runnable r,
	// long initialDelay, long delay) {
	// if (scheduled64Pool == null)
	// scheduled64Pool = Executors.newScheduledThreadPool(64);
	// return scheduled64Pool.scheduleWithFixedDelay(r, initialDelay, delay,
	// TimeUnit.MILLISECONDS);
	// }
	//
	// // 定时执行
	// public synchronized final static ScheduledFuture<?>
	// scheduled64FixedRate(Runnable r,
	// long initialDelay, long delay) {
	// if (scheduled64Pool == null)
	// scheduled64Pool = Executors.newScheduledThreadPool(64);
	// return scheduled64Pool.scheduleAtFixedRate(r, initialDelay, delay,
	// TimeUnit.MILLISECONDS);
	// }
	//
	// // 确定时分秒，每日执行
	// public synchronized final static ScheduledFuture<?>
	// scheduled64EveryDay(Runnable r,
	// int hour, int minute, int sec) {
	// if (scheduled64Pool == null)
	// scheduled64Pool = Executors.newScheduledThreadPool(64);
	// long now = now();
	// long tomorrow = beginningTomorrow();
	// long h = DateEx.hour();
	// long m = DateEx.minute();
	// long s = DateEx.second();
	// long initialDelay = 0;
	// long e1 = h * DateEx.TIME_HOUR + m * DateEx.TIME_MINUTE + s
	// * DateEx.TIME_SECOND;
	// long e2 = hour * DateEx.TIME_HOUR + minute * DateEx.TIME_MINUTE + sec
	// * DateEx.TIME_SECOND;
	// if (e1 < e2) {
	// initialDelay = e2 - e1;
	// } else {
	// initialDelay = tomorrow - now + e2;
	// }
	// long delay = DateEx.TIME_DAY;
	// return scheduled64Pool.scheduleAtFixedRate(r, initialDelay, delay,
	// TimeUnit.MILLISECONDS);
	// }

}
