package com.bowlong;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.bowlong.lang.NumEx;
import com.bowlong.lang.StrEx;
import com.bowlong.objpool.OutputArrayPool;
import com.bowlong.objpool.StringBufPool;
import com.bowlong.pinyin.PinYin;
import com.bowlong.util.DateEx;
import com.bowlong.util.FormatEx;
import com.bowlong.util.ListEx;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class Abstract {

	public static final Integer o = new Integer(0);

	public static final void println(Object... args) {
		print(args);
		System.out.println();
	}

	public static final void print(Object... args) {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			int length = args.length;
			int p = 0;
			for (Object o : args) {
				sb.append(o);
				p++;
				if (p < length)
					sb.append(", ");
			}
			System.out.print(sb);
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	// ///////////////////////////////////////////////////
	public static final Set newSet() {
		return Collections.synchronizedSet(new HashSet());
	}

	public static final Map newMap(int n) {
		return Collections.synchronizedMap(new HashMap(n));
	}

	public static final Map newMap() {
		return Collections.synchronizedMap(new HashMap());
	}

	public static final List newList(int n) {
		return Collections.synchronizedList(new Vector(n));
	}

	public static final List newList() {
		return Collections.synchronizedList(new ArrayList());
	}

	public static final List copyList(List l) {
		if (l == null)
			return null;
		List l2 = new Vector(l.size());
		l2.addAll(l);
		return l2;
	}

	public static final List toList(Map m) {
		List ret = newList();
		ret.addAll(m.values());
		return ret;
	}

	public static final List<Integer> toList(String s) {
		List<Integer> ret = newList();
		StringTokenizer st = new StringTokenizer(s, ",");
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			int e = NumEx.stringToInt(str.trim());
			if (e <= 0)
				continue;
			ret.add(e);
		}
		return ret;
	}

	public static final List<Integer> toList(int[] vars) {
		List ret = newList();
		for (int v : vars) {
			ret.add(v);
		}
		return ret;
	}

	public static final List<String> toList(String[] vars) {
		List ret = newList();
		for (String v : vars) {
			ret.add(v);
		}
		return ret;
	}

	public static final Map toMap(List l) {
		Map ret = newMap();
		int i = 1;
		for (Object o : l) {
			ret.put(i, o);
			i++;
		}
		return ret;
	}

	public static final Map toMap(String s) {
		Properties p = new Properties();
		try {
			StringReader sr = new StringReader(s);
			p.load(sr);
			return p;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	public static boolean in(int[] zbx, int id) {
		for (int i : zbx) {
			if (id == i)
				return true;
		}
		return false;
	}

	// 是否存在交集
	public static final boolean isIntersected(List l1, List l2) {
		return Collections.disjoint(l1, l2);
	}

	// 求交集:小心参数1会被改变
	public static final List intersected(List l1, List l2) {
		l1.retainAll(l2);
		return l1;
	}

	// ///////////////////////////////////////////////////
	public static final ByteArrayOutputStream newStream() {
		return new ByteArrayOutputStream();
	}

	public static final InputStream newStream(byte[] b) {
		return new ByteArrayInputStream(b);
	}

	// ///////////////////////////////////////////////////
	public static final Random random = new Random(System.currentTimeMillis());

	public static final boolean nextBool() {
		return random.nextBoolean();
	}

	public static final boolean nextBool(int max, int f) {
		int v = nextInt(max);
		return (v < f);
	}

	public static final <T> T rand(List objs) {
		int size = objs.size();
		if (size < 1)
			return null;
		else if (size == 1)
			return (T) objs.get(0);

		int v = random.nextInt(size - 1);
		return (T) objs.get(v);
	}

	public static final int nextInt(int max) {
		if (max <= 0)
			return 0;
		return random.nextInt(max);
	}

	public static final int nextInt(int f, int t) {
		if (t <= f)
			return f;
		return random.nextInt(t - f) + f;
	}

	// 对List进行打乱顺序
	public static final List random(List list) {
		List ret = new Vector();
		int num = list.size();
		for (int n = num; n > 0; n--) {
			int p = random.nextInt(n);
			Object e = list.remove(p);
			ret.add(e);
		}
		return ret;
	}

	public static String pn(int n) {
		return n > 0 ? "+" + n : "" + n;
	}

	public static final List<Map> sort(List<Map> m1, final String key) {
		return ListEx.sort(m1, key);
	}

	// ///////////////////////////////////////////////////
	public static final byte[] zip(byte[] b) throws IOException {
		ByteArrayOutputStream baos = OutputArrayPool.borrowObject();
		try {
			// ByteArrayOutputStream baos = newStream();
			GZIPOutputStream gos = new GZIPOutputStream(baos);
			// gos.write(0xFF);
			// gos.write(0xFE);
			gos.write(b);
			gos.finish();
			return baos.toByteArray();
		} finally {
			OutputArrayPool.returnObject(baos);
		}
	}

	public static final byte[] unzip(byte[] b) throws IOException {
		ByteArrayOutputStream baos = OutputArrayPool.borrowObject();
		try {
			byte[] buff = new byte[4 * 1024];
			// ByteArrayOutputStream baos = newStream();
			InputStream bais = newStream(b);
			GZIPInputStream gis = new GZIPInputStream(bais);
			int times = 1000;
			while (true) {
				if (times-- <= 0)
					break;

				int len = gis.read(buff);
				if (len <= 0)
					break;
				baos.write(buff, 0, len);
			}
			return baos.toByteArray();
		} finally {
			OutputArrayPool.returnObject(baos);
		}
	}

	public static final byte[] unzip(byte[] b, int srcLen) throws IOException {
		byte[] buff = new byte[srcLen];
		InputStream bais = newStream(b);
		GZIPInputStream gis = new GZIPInputStream(bais);
		gis.read(buff);
		return buff;
	}

	public static final byte[] readStream(InputStream is) {
		ByteArrayOutputStream out = OutputArrayPool.borrowObject();
		try {
			byte[] buf = new byte[1024];
			// ByteArrayOutputStream out = newStream();
			int times = 1000;
			while (true) {
				if (times-- <= 0)
					break;

				try {
					int len = is.read(buf);
					if (len <= 0)
						break;
					out.write(buf, 0, len);
				} catch (Exception e) {
				}
			}
			byte[] b = out.toByteArray();
			return b;
		} finally {
			OutputArrayPool.returnObject(out);
		}
	}

	// ///////////////////////////////////////////////////
	// 文件读写
	public static final byte[] readFully(File f) throws IOException {
		if (f == null || !f.exists())
			return null;
		int len = (int) f.length();
		byte[] b = new byte[len];
		FileInputStream fis = new FileInputStream(f);
		fis.read(b);
		fis.close();
		return b;
	}

	public static final void writeFully(File f, byte[] b) throws IOException {
		if (f == null)
			return;
		FileOutputStream fos = new FileOutputStream(f, false);
		fos.write(b);
		fos.close();
	}

	public static final String readStream(InputStream is, String charset)
			throws Exception {
		byte[] b = readStream(is);
		return new String(b, charset);
	}

	public static final String readFully(File f, String charset)
			throws IOException {
		byte[] b = readFully(f);
		return new String(b, charset);
	}

	public static final byte[] readResource(String fn) throws IOException {
		URL url = o.getClass().getResource(fn);
		String furl = url.getFile();
		File f = new File(furl);
		return readFully(f);
	}

	public static final void writeFully(File f, String v, String charset)
			throws IOException {
		byte[] b = v.getBytes(charset);
		writeFully(f, b);
	}

	// ///////////////////////////////////////////////////
	public static final String fmt_yyyy_MM_dd_HH_mm_ss_sss = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String fmt_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public static final String fmt_yyyy_MM_dd = "yyyy-MM-dd";
	public static final String fmt_HH_mm_ss = "HH:mm:ss";
	public static final String fmt_HH_mm = "HH:mm";
	public static final String fmt_MM_dd_HH_mm = "MM-dd HH:mm";
	public static final String fmtYyyyMm = "yyyyMM";
	public static final String fmtYyyyMmDd = "yyyyMMdd";

	public static final SimpleDateFormat sdf(String fmt) {
		return FormatEx.getFormat(fmt);
	}

	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm_ss_sss = sdf(fmt_yyyy_MM_dd_HH_mm_ss_sss);
	public static final SimpleDateFormat sdf_yyyy_MM_dd_HH_mm_ss = sdf(fmt_yyyy_MM_dd_HH_mm_ss);
	public static final SimpleDateFormat sdf_yyyy_MM_dd = sdf(fmt_yyyy_MM_dd);
	public static final SimpleDateFormat sdf_HH_mm = sdf(fmt_HH_mm);
	public static final SimpleDateFormat sdf_HH_mm_ss = sdf(fmt_HH_mm_ss);
	public static final SimpleDateFormat sdf_MM_dd_HH_mm = sdf(fmt_MM_dd_HH_mm);
	public static final SimpleDateFormat sdfYyyyMmDd = sdf(fmtYyyyMmDd);
	public static final SimpleDateFormat sdfYyyyMm = sdf(fmtYyyyMm);

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

	public static final long beginningTomorrow() {
		try {
			return beginningToday() + DateEx.TIME_DAY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static final Date beginningTomorrow2() {
		try {
			long tm = beginningTomorrow();
			Date d = new Date(tm);
			return d;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static final String beginningTomorrow3() {
		try {
			Date d = beginningTomorrow2();
			String s = sdf_yyyy_MM_dd.format(d);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static final long now() {
		return System.currentTimeMillis();
	}

	public static final Date date() {
		return new Date();
	}

	public static final String yearMMdd() {
		return sdf_yyyy_MM_dd.format(new Date());
	}

	public static final String yearMMddHHmmss() {
		return sdf_yyyy_MM_dd_HH_mm_ss.format(new Date());
	}

	public static final String hourMm() {
		return sdf_HH_mm.format(new Date());
	}

	public static final String monthDdHHmm() {
		return sdf_MM_dd_HH_mm.format(new Date());
	}

	// 两个时间的时间差
	public static final long timeDifference(Date d1, Date d2) {
		long l1 = d1.getTime();
		long l2 = d2.getTime();
		return l2 - l1;
	}

	// 延迟时间
	public static final Date delay(Date d, long delay) {
		return new Date(d.getTime() + delay);
	}

	// 当前时间之后某个时间
	public static final Date delay(long delay) {
		return new Date(now() + delay);
	}

	// 现在位于时间段内
	public static boolean isBetween(Date kssj, Date jssj) {
		Date now = new Date();
		if (now.before(kssj))
			return false;
		if (now.after(jssj))
			return false;
		return true;
	}

	// 过去
	public static boolean isPast(Date d) {
		Date now = new Date();
		return d.before(now);
	}

	// 未来
	public static boolean isFuture(Date d) {
		Date now = new Date();
		return d.after(now);
	}

	public static final String s(String s, Object... args) {
		return String.format(s, args);
	}

	public static final void s(StringBuffer sb, String s, Object... args) {
		String s2 = String.format(s, args);
		sb.append(s2);
	}

	public static final String sn(String s, Object... args) {
		return String.format(s + "\r\n", args);
	}

	public static final void sn(StringBuffer sb, String s, Object... args) {
		String s2 = String.format(s + "\r\n", args);
		sb.append(s2);
	}

	public static final String fmt(String s, Object... args) {
		return String.format(s, args);
	}

	public static final String format(String s, Object... args) {
		return String.format(s, args);
	}

	// 带1位小数
	public static final String n2s(int i) {
		if (i < 1000)
			return i + "";
		if (i < 1000 * 10)
			return String.format("%.1fK", ((double) i / 1000));
		if (i < 1000 * 1000)
			return String.format("%.1fW", ((double) i / 10000));
		if (i < 1000 * 1000 * 1000)
			return String.format("%.1fM", ((double) i / (1000 * 1000)));
		return String.format("%.1fG", ((double) i / (1000 * 1000 * 1000)));
	}

	// 自动识别是否带小数
	public static final String n2sn(long i) {
		if (i < 1000)
			return i + "";
		if (i < 1000 * 10)
			return String.format("%.1fK", ((double) i / 1000));
		if (i < 1000 * 1000)
			return String.format("%.0fW", ((double) i / 10000));
		if (i < 1000 * 1000 * 1000)
			return String.format("%.0fM", ((double) i / (1000 * 1000)));
		return String.format("%.1fG", ((double) i / (1000 * 1000 * 1000)));
	}

	// 带小数,支持负数
	public static final String n(int i) {
		boolean abs = false;
		if (i < 0) {
			i = -i;
			abs = true;
		}
		String s = n2s(i);
		String r = abs ? ('-' + s) : s;
		return r;
	}

	// ///////////////////////////////////////////////////
	// 计算两点间距离
	public final static int distance(int x1, int y1, int x2, int y2) {
		double v = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
		return (int) v;
	}

	// 计算百分率
	public static final int percent(double v, double max) {
		if (v <= 0 || max <= 0)
			return 0;
		int r = (int) (v * 100 / max);
		return r > 100 ? 100 : r;
	}

	// ///////////////////////////////////////////////////
	public static final int argb(Color c) {
		return argb(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
	}

	public static final int argb(int a, int r, int g, int b) {
		return (r << 24) + (r << 16) + (g << 8) + b;
	}

	public static final int[] argb(long argb) {
		int a = (byte) ((argb >> 24) & 0xff);
		int r = (byte) ((argb >> 16) & 0xff);
		int g = (byte) ((argb >> 8) & 0xff);
		int b = (byte) ((argb >> 0) & 0xff);
		int[] v = { a, r, g, b };
		return v;
	}

	// ///////////////////////////////////////////////////
	public static final int rgb(Color c) {
		return rgb(c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue());
	}

	public static final int rgb(int a, int r, int g, int b) {
		return (r << 16) + (g << 8) + b;
	}

	public static final int[] rgb(int rgb) {
		int r = (byte) ((rgb >> 16) & 0xff);
		int g = (byte) ((rgb >> 8) & 0xff);
		int b = (byte) ((rgb >> 0) & 0xff);
		int[] v = { r, g, b };
		return v;
	}

	// ///////////////////////////////////////////////////
	public static final boolean isNull(Object o) {
		return o == null;
	}

	public static final boolean isEmpty(byte[] o) {
		return o == null || o.length <= 0;
	}

	public static final boolean isEmpty(List o) {
		return o == null || o.isEmpty();
	}

	public static final boolean isEmpty(Map o) {
		return o == null || o.isEmpty();
	}

	public static final boolean isEmpty(String o) {
		return o == null || o.isEmpty();
	}

	public static final boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
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

	// ///////////////////////////////////////////////////

	// public static Executor singleThreadExecutor = null;
	// public static Executor fixed4ThreadPool = null;
	// public static ScheduledExecutorService scheduled4Pool = null;
	//
	// public static final void executeSingle(Runnable r) {
	// if (singleThreadExecutor == null)
	// singleThreadExecutor = ThreadEx.newSingleThreadExecutor();
	// singleThreadExecutor.execute(r);
	// }
	//
	// public static final void execute4Fixed(Runnable r) {
	// if (fixed4ThreadPool == null)
	// fixed4ThreadPool = ThreadEx.newFixedThreadPool(4);
	// fixed4ThreadPool.execute(r);
	// }
	//
	// // 延时执行
	// public static final ScheduledFuture<?> scheduled4(Runnable r, long delay)
	// {
	// if (scheduled4Pool == null)
	// scheduled4Pool = ThreadEx.newScheduledPool(4);
	// return scheduled4Pool.schedule(r, delay, TimeUnit.MILLISECONDS);
	// }
	//
	// // 定间隔时间执行
	// public static final ScheduledFuture<?> scheduled4FixedDelay(Runnable r,
	// long initialDelay, long delay) {
	// if (scheduled4Pool == null)
	// scheduled4Pool = ThreadEx.newScheduledPool(4);
	//
	// return scheduled4Pool.scheduleWithFixedDelay(r, initialDelay, delay,
	// TimeUnit.MILLISECONDS);
	// }
	//
	// // 确定时分秒，每日执行
	// public static final ScheduledFuture<?> scheduled4EveryDay(Runnable r,
	// int hour, int minute, int sec) {
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
	// return scheduled4FixedRate(r, initialDelay, delay);
	// }
	//
	// // 定时执行
	// public static final ScheduledFuture<?> scheduled4FixedRate(Runnable r,
	// long initialDelay, long delay) {
	// if (scheduled4Pool == null)
	// scheduled4Pool = ThreadEx.newScheduledPool(4);
	//
	// return scheduled4Pool.scheduleAtFixedRate(r, initialDelay, delay,
	// TimeUnit.MILLISECONDS);
	// }

	public static final boolean isTimeout(long LASTTIME, long TIMEOUT) {
		if (TIMEOUT <= 0)
			return false;
		long l2 = System.currentTimeMillis();
		long t = l2 - LASTTIME;
		return (t > TIMEOUT);
	}

	public static final boolean isTimeout(Date DLASTTIME, long TIMEOUT) {
		if (TIMEOUT <= 0)
			return false;
		long LASTTIME = DLASTTIME.getTime();
		return isTimeout(LASTTIME, TIMEOUT);
	}

	public static final String getAppRoot() {
		return System.getProperty("user.dir");
	}

	public static Map getMap(Map m, Object key) {
		Object v = m.get(key);
		if (v == null)
			v = newMap();
		return (Map) v;
	}

	public static List getList(Map m, Object key) {
		Object v = m.get(key);
		if (v == null)
			v = newList();
		return (List) v;
	}

	public static String getString(Map m, String key) {
		Object o = m.get(key);
		if (o == null)
			return "";
		else if (o instanceof String) {
			String v = (String) o;
			return v;
		} else {
			return String.valueOf(o);
		}
	}

	public static boolean getBool(Map m, String key) {
		try {
			Object o = m.get(key);
			if (o == null)
				return false;
			if (o instanceof Boolean)
				return ((Boolean) o).booleanValue();
			else if (o instanceof String) {
				String v = (String) o;
				return Boolean.parseBoolean(v);
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public static int getInt(Map m, String key) {
		try {
			Object o = m.get(key);
			if (o == null)
				return 0;
			if (o instanceof Integer)
				return ((Integer) o).intValue();
			else if (o instanceof String) {
				String v = (String) o;
				return Integer.parseInt(v);
			}
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	public static double getDouble(Map m, String key) {
		try {
			Object o = m.get(key);
			if (o == null)
				return 0;
			if (o instanceof Double)
				return ((Double) o).doubleValue();
			else if (o instanceof String) {
				String v = (String) o;
				return Double.parseDouble(v);
			} else if (o instanceof Integer) {
				Integer v = (Integer) o;
				return ((Integer) v).doubleValue();
			}
			return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	public static final String getShortPinYin(String s) {
		return PinYin.getShortPinYin(s);
	}

	public static final String upperFirst(String s) {
		return StrEx.upperFirst(s);
	}
}
