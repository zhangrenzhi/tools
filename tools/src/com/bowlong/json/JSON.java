package com.bowlong.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import com.bowlong.bio2.B2InputStream;
import com.bowlong.bio2.B2OutputStream;
import com.bowlong.io.FileEx;
import com.bowlong.objpool.StringBufPool;
import com.bowlong.security.Base64;
import com.bowlong.util.FormatEx;
import com.bowlong.util.ListEx;
import com.bowlong.util.MapEx;

// 大数据时候比FastJSON慢

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JSON {
	private static JSON my = new JSON();

	private static final SimpleDateFormat myFmt = FormatEx
			.getFormat("yyyy-MM-dd HH:mm:ss");

	public static final void toJSONString(Object v, File toF)
			throws IOException {
		String str = toJSONString(v);
		FileOutputStream fos = new FileOutputStream(toF);
		OutputStreamWriter osw = new OutputStreamWriter(fos,
				Charset.forName("UTF-8"));
		osw.write(str);
		osw.flush();
		osw.close();
		fos.close();
	}

	public static final String toJSONString(Object v) throws IOException {
		if (v == null)
			return "null";
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			write(sb, v);
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	private static final void write(StringBuffer sb, Map v) throws IOException {
		if (v == null) {
			sb.append("null");
			return;
		}

		int i = 0;
		int size = v.size();
		Set<Entry> entrys = v.entrySet();
		sb.append("{");
		for (Entry e : entrys) {
			i++;
			Object key = e.getKey();
			Object var = e.getValue();
			write(sb, key);
			sb.append(":");
			write(sb, var);
			if (i < size) {
				sb.append(",");
			}
		}
		sb.append("}");
	}

	private static final void write(StringBuffer sb, List v) throws IOException {
		if (v == null) {
			sb.append("null");
			return;
		}

		int size = v.size();
		sb.append("[");
		for (int i = 0; i < size; i++) {
			Object obj = v.get(i);
			write(sb, obj);
			if (i < size - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
	}

	private static final void write(StringBuffer sb, Object obj)
			throws IOException {
		if (obj == null) {
			sb.append("null");
		} else if (obj instanceof Boolean) {
			sb.append(((Boolean) obj).booleanValue());
		} else if (obj instanceof Integer) {
			sb.append(((Integer) obj).intValue());
		} else if (obj instanceof String) {
			String s = (String) obj;
			s = s.replace("\\", "\\\\");
			s = s.replace("\"", "\\\"");
			s = s.replace("/", "\\/");
			s = s.replace("\b", "\\b");
			s = s.replace("\f", "\\f");
			s = s.replace("\n", "\\n");
			s = s.replace("\r", "\\r");
			s = s.replace("\t", "\\t");
			sb.append("\"").append(s).append("\"");
		} else if (obj instanceof List) {
			write(sb, ((List) obj));
		} else if (obj instanceof Map) {
			write(sb, ((Map) obj));
		} else if (obj instanceof Byte) {
			sb.append(((Byte) obj).byteValue());
		} else if (obj instanceof Short) {
			sb.append(((Short) obj).shortValue());
		} else if (obj instanceof Long) {
			sb.append(((Long) obj).longValue());
		} else if (obj instanceof Float) {
			sb.append(((Float) obj).floatValue());
		} else if (obj instanceof Double) {
			sb.append(((Double) obj).doubleValue());
		} else if (obj instanceof Date) {
			Date d = (Date) obj;
			sb.append("\"").append(myFmt.format(d)).append("\"");
		} else if (obj instanceof byte[]) {
			byte[] buf = (byte[]) obj;
			String s = Base64.encode(buf);
			sb.append("\"").append(s).append("\"");
		} else {
			throw new IOException("No Support Type :"
					+ obj.getClass().getName() + "  " + obj);
		}
	}

	// /////////////////////////////////////////////////////
	private class Pointer {
		public Pointer(int i) {
			this.i = i;
		}

		public int i;
	}

	private static class KV {
		boolean inString = false;
		boolean isEmpty = true;
		public static final int STEP_KEY = 0;
		public static final int STEP_VALUE = 1;

		public static final int V_STRING = 0;
		public static final int V_LIST = 1;
		public static final int V_MAP = 2;

		public int step = STEP_KEY;
		public int vType = V_STRING;

		public StringBuffer key = new StringBuffer();
		public StringBuffer vStr = new StringBuffer();
		public List vList = new Vector();
		public Map vMap = new HashMap();

		public void append(char c) {
			isEmpty = false;
			if (step == STEP_KEY) {
				key.append(c);
			} else if (step == STEP_VALUE && vType == V_STRING) {
				vStr.append(c);
			}
		}

		public void putTo(Map map) {
			if (isEmpty)
				return;
			String k = key.toString();

			if (vType == V_STRING) {
				String v = vStr.toString();
				if (k == null || v == null)
					return;
				if (k.isEmpty() && v.isEmpty())
					return;

				map.put(toObject(k), toObject(v));
			} else if (vType == V_LIST) {
				map.put(toObject(k), vList);
			} else if (vType == V_MAP) {
				map.put(toObject(k), vMap);
			}
		}

		public String toString() {
			if (vType == V_STRING) {
				return "[" + key.toString() + ":" + vStr.toString() + "]";
			} else if (vType == V_LIST) {
				return "[" + key.toString() + ":" + vList + "]";
			} else if (vType == V_MAP) {
				return "[" + key.toString() + ":" + vMap + "]";
			}
			return "";
		}

	}

	private KV newKV() {
		return new KV();
	}

	private Pointer newP(int i) {
		return new Pointer(i);
	}

	private static final Object toObject(String s) {
		if (s == null)
			return s;
		s = s.trim();
		if (s.isEmpty())
			return s;
		if (s.contains("null"))
			return null;
		char b1 = s.charAt(0);
		char b2 = s.charAt(s.length() - 1);
		if (b1 == '\"' && b2 == '\"') {
			s = s.substring(1, s.length() - 1);
			s = s.replace("\\\"", "\"");
			s = s.replace("\\/", "/");
			s = s.replace("\\b", "\b");
			s = s.replace("\\f", "\f");
			s = s.replace("\\n", "\n");
			s = s.replace("\\r", "\r");
			s = s.replace("\\t", "\t");
			s = s.replace("\\\\", "\\");
			return s;
		} else {
			try {
				return Integer.parseInt(s);
			} catch (Exception e1) {
				try {
					return Long.parseLong(s);
				} catch (Exception e2) {
					try {
						return Double.parseDouble(s);
					} catch (Exception e3) {
						try {
							if (s.toLowerCase().equals("true"))
								return true;
							else if (s.toLowerCase().equals("false"))
								return false;
							throw new Exception();
						} catch (Exception e) {
						}
					}
				}
			}
		}

		return s;
	}

	public static final Object parse(File f) throws IOException {
		String s = FileEx.readAll(f);
		return JSON.parse(s);
	}

	public static final <T> T parseGeneric(File s) throws IOException {
		Object o = parse(s);
		return (T) o;
	}

	public static final <T> T parseGeneric(String s) throws IOException {
		Object o = parse(s);
		return (T) o;
	}

	public static final Map parseMap(String s) throws IOException {
		Object o = parse(s);
		return (Map) o;
	}

	public static final Map toMap(String s) {
		try {
			return parseMap(s);
		} catch (Exception e) {
		}
		return new Hashtable();
	}

	public static final List parseList(String s) throws IOException {
		Object o = parse(s);
		return (List) o;
	}

	public static final List toList(String s) {
		try {
			return parseList(s);
		} catch (Exception e) {
		}
		return new Vector();
	}

	public static final Object parse(String s) throws IOException {
		if (s == null || s.isEmpty())
			throw new IOException("s isEmpty!!");

		// 如果前面有空值则跳过 空格 和 换行
		int p1 = 0;
		do {
			if (s.charAt(p1) == '{' || s.charAt(p1) == '[')
				break;
			p1++;
		} while (true);

		Pointer p = my.newP(p1);
		if (s.charAt(p1) == '{') {
			return parseMap(s, p);
		} else if (s.charAt(p1) == '[') {
			return parseList(s, p);
		}

		throw new IOException("No Support.");
	}

	public static final byte[] toBIO2(String s) throws Exception {
		Object object = parse(s);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		B2OutputStream.writeObject(os, object);
		return os.toByteArray();
	}

	public static final String parseFromBIO2(byte[] b) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(b);
		Object object = B2InputStream.readObject(is);
		return toJSONString(object);
	}

	private static final List parseList(String s, Pointer p) throws IOException {
		if (s == null || s.isEmpty())
			throw new IOException("s isEmpty!!");

		boolean F = true;
		boolean isObject = false;
		List list = new Vector();
		boolean inString = false;
		StringBuffer buff = new StringBuffer();
		int length = s.length();
		for (; p.i < length; p.i++) {
			char c = s.charAt(p.i);
			if (c == '[' && !inString) {
				if (F) {
					F = false;
				} else {
					List list2 = parseList(s, p);
					list.add(list2);
				}
			} else if (c == ']' && !inString) {
				if (buff.length() > 0) {
					String str = buff.toString();
					list.add(toObject(str));
					p.i++;
				}
				return list;
			} else if (c == ',' && !inString) {
				String str = buff.toString();
				if (isObject && str.isEmpty()) {
					isObject = false;
				} else {
					list.add(toObject(str));
					buff = new StringBuffer();
				}
			} else if (c == '{' && !inString) {
				Map map = parseMap(s, p);
				list.add(map);
				isObject = true;
				buff = new StringBuffer();
			} else if (c == '\r' || c == '\n') {
				continue;
			} else {
				if (c == '\"')
					inString = !inString;

				buff.append(c);
			}
		}
		return list;
	}

	private static final Map parseMap(String s, Pointer p) throws IOException {
		if (s == null || s.isEmpty())
			throw new IOException("s isEmpty!!");

		boolean F = true;
		Map map = new HashMap();
		KV kv = my.newKV();
		int length = s.length();
		for (; p.i < length; p.i++) {
			char c = s.charAt(p.i);
			if (c == '{' && !kv.inString) {
				if (F) {
					F = false;
				} else {
					kv.vType = KV.V_MAP;
					kv.vMap = parseMap(s, p);
					kv.putTo(map);

					kv = my.newKV();
				}
			} else if (c == '}' && !kv.inString) {
				kv.putTo(map);
				return map;
			} else if (c == ':' && !kv.inString) {
				kv.step = KV.STEP_VALUE;
			} else if (c == ',' && !kv.inString) {
				kv.putTo(map);
				kv = my.newKV();
			} else if (c == '[' && !kv.inString) {
				kv.vType = KV.V_LIST;
				kv.vList = parseList(s, p);
				kv.putTo(map);

				kv = my.newKV();
			} else if (c == '\r' || c == '\n') {
				continue;
			} else {
				if (c == '\"')
					kv.inString = !kv.inString;

				if (kv.step == KV.STEP_KEY) {
					kv.append(c);
				} else if (kv.step == KV.STEP_VALUE) {
					kv.append(c);
				}
			}
		}
		return map;
	}

	public static String formatString(final String s) throws IOException {
		Object obj = parse(s);
		if (obj instanceof Map) {
			Map m = (Map) obj;
			return MapEx.formatString(m);
		} else if (obj instanceof List) {
			List l = (List) obj;
			return ListEx.formatString(l);
		}
		return "No Support. (Only for Map or List)";
	}

	public static String formatString(final Map m) throws IOException {
		return MapEx.formatString(m);
	}

	public static String formatString(final List l) throws IOException {
		return ListEx.formatString(l);
	}

	// /////////////////////////////////////////////////////

	public static void main(String[] args) throws IOException {
		List l = new Vector();
		l.add(null);
		l.add(false);
		l.add((byte) 1);
		l.add((short) 2);
		l.add((int) 3);
		l.add((long) 4);
		l.add((float) 3.14);
		l.add((double) 3.1416);
		l.add("xxx-作为:aa");

		List l2 = new Vector();
		l2.add("l2你好");
		l.add(l2);

		List l3 = new Vector();
		l3.add("l3{[[你}好");
		l2.add(l3);

		int[] arr = new int[2];
		arr[0] = 1;
		arr[1] = 2;
		Map m = new HashMap();
		m.put(0, null);
		m.put(1, true);
		m.put(2, (byte) 12);
		m.put(3, (short) 12);
		m.put(4, (int) 12);
		m.put(5, (long) 12);
		m.put(6, (float) 3.14);
		m.put(7, (double) 3.14);
		m.put(8, "3str");
		// m.put(9, "3str".getBytes());
		m.put(10, l);
		// m.put(11, arr);
		// m.put(12, new Date());
		// l2.add(m);

		// String s1 = JSON.toJSONString(m);
		// Map map = new HashMap();
		// Object o = JSON.parseMap(s1, 0);
		// System.out.println(s1);
		// System.out.println(o.toString().replace(" ", ""));

		{
			String s2 = JSON.toJSONString(m);
			System.out.println(s2);
			System.out.println(formatString(m));
			// s2 = "[{1ss:\"123\",2xx:3.24},{4:\"123\", 5 :true}]";

			Map list = (Map) JSON.parse(s2);
			System.out.println(list.toString());
			System.out.println(toJSONString(list));
		}

		String ss = "{\"hey\": \"guy\",\"anumber\": 243,\"anobject\": {\"whoa\": \"nuts\",\"anarray\": [1,2,\"thr<h1>ee\"], \"more\":\"stuff\"},\"awesome\": true,\"bogus\": false,\"meaning\": null, \"japanese\":\"明日がある。\", \"link\": \"http://jsonview.com\", \"notLink\": \"http://jsonview.com is great\"}";
		System.out.println("MAP:" + formatString(ss));
	}
}
