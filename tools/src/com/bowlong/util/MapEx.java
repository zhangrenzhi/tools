package com.bowlong.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.bowlong.lang.NumEx;
import com.bowlong.objpool.StringBufPool;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapEx {

	public static final MapBuilder builder() {
		return MapBuilder.builder();
	}

	public static final MapBuilder builder(Map map) {
		return MapBuilder.builder(map);
	}

	public static final Map newMap() {
		return new Hashtable();
	}

	public static final Map newHashMap() {
		return new HashMap();
	}

	public static final Hashtable newHashtable() {
		return new Hashtable();
	}

	public static final ConcurrentHashMap newConcurrentHashMap() {
		return new ConcurrentHashMap();
	}

	public static final boolean isEmpty(Map map){
		return (map == null || map.isEmpty());
	}

	public static final boolean notEmpty(Map map){
		return (map != null && !map.isEmpty());
	}

	public static final <T> T copyValue(Map from, Map to, Object key) {
		T v = get(from, key);
		if (v == null)
			return null;
		to.put(key, v);
		return v;
	}

	public static final <T> T get(Map map, Object key) {
		return (T) map.get(key);
	}

	public static final boolean getBoolean(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return false;
		if (v instanceof Boolean) {
			return ((Boolean) v).booleanValue();
		} else if (v instanceof Integer) {
			return ((Integer) v) <= 0 ? false : true;
		} else if (v instanceof Long) {
			return ((Long) v) <= 0 ? false : true;
		} else if (v instanceof String) {
			return NumEx.stringToBool((String) v);
		}
		return false;
	}

	public static final byte getByte(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return 0;
		if (v instanceof Byte) {
			return ((Byte) v).byteValue();
		} else if (v instanceof String) {
			return NumEx.stringToByte((String) v);
		}
		return 0;
	}

	public static final short getShort(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return 0;
		if (v instanceof Byte) {
			return ((Byte) v).shortValue();
		} else if (v instanceof Short) {
			return ((Short) v).shortValue();
		} else if (v instanceof Integer) {
			return ((Integer) v).shortValue();
		} else if (v instanceof Long) {
			return ((Long) v).shortValue();
		} else if (v instanceof Float) {
			return ((Float) v).shortValue();
		} else if (v instanceof Double) {
			return ((Double) v).shortValue();
		} else if (v instanceof String) {
			return NumEx.stringToShort((String) v);
		} else if (v instanceof BigInteger) {
			return ((BigInteger) v).shortValue();
		} else if (v instanceof BigDecimal) {
			return ((BigDecimal) v).shortValue();
		}
		return 0;
	}

	public static final int getInt(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return 0;
		if (v instanceof Byte) {
			return ((Byte) v).intValue();
		} else if (v instanceof Short) {
			return ((Short) v).intValue();
		} else if (v instanceof Integer) {
			return ((Integer) v).intValue();
		} else if (v instanceof Long) {
			return ((Long) v).intValue();
		} else if (v instanceof Float) {
			return ((Float) v).intValue();
		} else if (v instanceof Double) {
			return ((Double) v).intValue();
		} else if (v instanceof String) {
			return NumEx.stringToInt((String) v);
		} else if (v instanceof BigInteger) {
			return ((BigInteger) v).intValue();
		} else if (v instanceof BigDecimal) {
			return ((BigDecimal) v).intValue();
		}
		return 0;
	}

	public static final long getLong(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return 0;
		if (v instanceof Byte) {
			return ((Byte) v).longValue();
		} else if (v instanceof Short) {
			return ((Short) v).longValue();
		} else if (v instanceof Integer) {
			return ((Integer) v).longValue();
		} else if (v instanceof Long) {
			return ((Long) v).longValue();
		} else if (v instanceof Float) {
			return ((Float) v).longValue();
		} else if (v instanceof Double) {
			return ((Double) v).longValue();
		} else if (v instanceof String) {
			return NumEx.stringToLong((String) v);
		} else if (v instanceof BigInteger) {
			return ((BigInteger) v).longValue();
		} else if (v instanceof BigDecimal) {
			return ((BigDecimal) v).longValue();
		}
		return 0;
	}

	public static final float getFloat(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return (float) 0.0;
		if (v instanceof Byte) {
			return ((Byte) v).floatValue();
		} else if (v instanceof Short) {
			return ((Short) v).floatValue();
		} else if (v instanceof Integer) {
			return ((Integer) v).floatValue();
		} else if (v instanceof Long) {
			return ((Long) v).floatValue();
		} else if (v instanceof Float) {
			return ((Float) v).floatValue();
		} else if (v instanceof Double) {
			return ((Double) v).floatValue();
		} else if (v instanceof String) {
			return NumEx.stringToFloat((String) v);
		} else if (v instanceof BigInteger) {
			return ((BigInteger) v).floatValue();
		} else if (v instanceof BigDecimal) {
			return ((BigDecimal) v).floatValue();
		}
		return 0;
	}

	public static final double getDouble(Map map, Object key) {
		Object v = map.get(key);
		if (v == null)
			return 0.0;
		if (v instanceof Byte) {
			return ((Byte) v).doubleValue();
		} else if (v instanceof Short) {
			return ((Short) v).floatValue();
		} else if (v instanceof Integer) {
			return ((Integer) v).doubleValue();
		} else if (v instanceof Long) {
			return ((Long) v).doubleValue();
		} else if (v instanceof Float) {
			return ((Float) v).doubleValue();
		} else if (v instanceof Double) {
			return ((Double) v).doubleValue();
		} else if (v instanceof String) {
			return NumEx.stringToDouble((String) v);
		} else if (v instanceof BigInteger) {
			return ((BigInteger) v).doubleValue();
		} else if (v instanceof BigDecimal) {
			return ((BigDecimal) v).doubleValue();
		}
		return 0;
	}

	public static final BigInteger getBigInteger(Map map, Object key) {
		return (BigInteger) map.get(key);
	}

	public static final BigDecimal getBigDecimal(Map map, Object key) {
		return (BigDecimal) map.get(key);
	}

	public static final String getString(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return "";
		else if (obj instanceof String)
			return (String) obj;

		return String.valueOf(obj);
	}

	public static final Date getDate(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return null;
		if (obj instanceof Date)
			return (Date) obj;
		else if (obj instanceof NewDate)
			return (NewDate) obj;
		else if (obj instanceof java.sql.Date)
			return new Date(((java.sql.Date) obj).getTime());
		else if (obj instanceof java.sql.Timestamp)
			return new Date(((java.sql.Timestamp) obj).getTime());
		else if (obj instanceof Long)
			return new Date((Long) obj);
		else if (obj instanceof String)
			return NewDate.parse2((String) obj, DateEx.fmt_yyyy_MM_dd_HH_mm_ss);

		return null;
	}

	public static final NewDate getNewDate(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return null;
		if (obj instanceof NewDate)
			return (NewDate) obj;
		else if (obj instanceof Date)
			return new NewDate((Date) obj);
		else if (obj instanceof java.sql.Date)
			return new NewDate(((java.sql.Date) obj));
		else if (obj instanceof java.sql.Timestamp)
			return new NewDate(((java.sql.Timestamp) obj));
		else if (obj instanceof Long)
			return new NewDate((Long) obj);
		else if (obj instanceof String)
			return NewDate.parse2((String) obj, DateEx.fmt_yyyy_MM_dd_HH_mm_ss);

		return null;
	}

	public static final byte[] getByteArray(Map map, Object key) {
		return (byte[]) map.get(key);
	}

	public static final Map getMap(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return newMap();

		if (obj instanceof NewMap)
			return (NewMap) obj;
		else if (obj instanceof Map)
			return (Map) obj;

		return newMap();
	}

	public static final NewMap getNewMap(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return new NewMap();

		if (obj instanceof NewMap)
			return (NewMap) obj;
		else if (obj instanceof Map)
			return NewMap.create((Map) obj);

		return new NewMap();
	}

	public static final Set getSet(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return new HashSet();

		if (obj instanceof NewSet)
			return (NewSet) map.get(key);
		else if (obj instanceof Set)
			return (Set) map.get(key);

		return new HashSet();
	}

	public static final NewSet getNewSet(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return new NewSet();

		if (obj instanceof NewSet)
			return (NewSet) obj;
		else if (obj instanceof NewSet)
			return NewSet.create((Set) obj);

		return new NewSet();
	}

	public static final List getList(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return new Vector();

		if (obj instanceof NewList)
			return (NewList) obj;
		else if (obj instanceof List)
			return (List) obj;

		return new Vector();
	}

	public static final NewList getNewList(Map map, Object key) {
		Object obj = map.get(key);
		if (obj == null)
			return new NewList();

		if (obj instanceof NewList)
			return (NewList) obj;
		else if (obj instanceof List)
			return NewList.create((List) obj);

		return new NewList();
	}

	public static final Map toMap(List l) {
		Map ret = newMap();
		if (l == null || l.isEmpty())
			return ret;
		for (int i = 0; i < l.size(); i++) {
			Object o = l.get(i);
			ret.put(i, o);
		}
		return ret;
	}

	public static final Map toHashMap(Map map) {
		Map ret = newHashMap();
		ret.putAll(map);
		return ret;
	}

	public static final Map toHashtable(Map map) {
		Map ret = newHashtable();
		ret.putAll(map);
		return ret;
	}

	public static final Map toConcurrentHashMap(Map map) {
		Map ret = newConcurrentHashMap();
		ret.putAll(map);
		return ret;
	}

	public static final List keyToList(Map map) {
		List list = ListEx.newList();
		list.addAll(map.keySet());
		return list;
	}

	public static final List valueToList(Map map) {
		List list = ListEx.newList();
		list.addAll(map.values());
		return list;
	}

	public static final Map toMap(Object[] array) {
		if (array == null) {
			return null;
		}
		final Map map = new HashMap((int) (array.length * 1.5));
		for (int i = 0; i < array.length; i++) {
			Object object = array[i];
			if (object instanceof Map.Entry) {
				Map.Entry entry = (Map.Entry) object;
				map.put(entry.getKey(), entry.getValue());
			} else if (object instanceof Object[]) {
				Object[] entry = (Object[]) object;
				if (entry.length < 2) {
					throw new IllegalArgumentException("Array element " + i
							+ ", '" + object + "', has a length less than 2");
				}
				map.put(entry[0], entry[1]);
			} else {
				throw new IllegalArgumentException("Array element " + i + ", '"
						+ object
						+ "', is neither of type Map.Entry nor an Array");
			}
		}
		return map;
	}

	public static final Map toHashMap(Object[] array) {
		Map m = toMap(array);
		return toHashMap(m);
	}

	public static final Map toHashtable(Object[] array) {
		Map m = toMap(array);
		return toHashtable(m);
	}

	public static final Map toConcurrentHashMap(Object[] array) {
		Map m = toMap(array);
		return toConcurrentHashMap(m);
	}

	public static final Map propertiesToMap(String s) {
		Properties p = new Properties();
		try {
			StringReader sr = new StringReader(s);
			BufferedReader br = new BufferedReader(sr);
			p.load(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	public static final Properties load(File f) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		return load(fis);
	}

	public static final Properties load(InputStream is) throws Exception {
		InputStreamReader isr = new InputStreamReader(is);
		return load(isr);
	}

	public static final Properties load(Reader br) throws Exception {
		Properties p = new Properties();
		try {
			p.load(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return p;
	}

	public static final void save(File f, Properties p) throws Exception {
		FileOutputStream fos = new FileOutputStream(f);
		save(fos, p);
	}

	public static final void save(OutputStream os, Properties p)
			throws Exception {
		p.store(os, "");
	}

	public static final String formatString(final Map m) {
		int depth = 1;
		return formatString(m, depth);
	}

	public static final String formatString(final Map m, int depth) {
		int p = 0;
		int size = m.size();
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("{\n");
			Set<Entry> entrys = m.entrySet();
			for (Entry e : entrys) {
				Object k = e.getKey();
				Object v = e.getValue();
				for (int i = 0; i < depth; i++)
					sb.append("    ");
				if (k instanceof String) {
					sb.append("\"").append(k).append("\"");
				} else {
					sb.append(k);
				}
				sb.append(":");
				if (v instanceof Map) {
					sb.append(formatString((Map) v, depth + 1));
				} else if (v instanceof List) {
					sb.append(ListEx.formatString((List) v, depth + 1));
				} else if (v instanceof String) {
					sb.append("\"").append(v).append("\"");
				} else {
					sb.append(v);
				}
				p++;
				if (p < size) {
					sb.append(",");
				}
				sb.append("\n");
			}
			for (int i = 1; i < depth; i++)
				sb.append("    ");
			sb.append("}");
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static <T> T getKey(Map map) throws Exception {
		if (map == null || map.isEmpty())
			return null;
		Entry<Object, Object> E = ((Map<Object, Object>) map).entrySet()
				.iterator().next();
		return (T) E.getKey();
	}

	public static <T> T getValue(Map map) throws Exception {
		if (map == null || map.isEmpty())
			return null;
		Entry<Object, Object> E = ((Map<Object, Object>) map).entrySet()
				.iterator().next();
		return (T) E.getValue();
	}

	public static void main(String[] args) {
		// 也支持MapEx.builder(map)传入一个map的情况,增减数据
		Map m = MapEx.builder().put("abc", "abc").put(123, 123).put(0.13, true)
				.toMap();
		Map m2 = MapEx.builder().put("abc2", "abc2").put(123, 123)
				.put(0.13, true).toMap();
		// System.out.println(m);
		// {0.13=true, abc=abc, 123=123}

		// 也支持ListEx.builder(list)传入一个list的情况,增减数据
		List l = ListEx.builder().add(123).add("abc").add(false).toList();
		System.out.println(ListEx.formatString(l));
		// [123, abc, false]
		m.put("m2", m2);
		m.put("list", l);
		System.out.println(formatString(m));

		// Map colorMap = toMap(new Object[][] { { "RED", 0xFF0000 }, { "GREEN",
		// 0x00FF00 }, { "BLUE", 0x0000FF } });
		//
		// System.out.println(colorMap);
		//
		// int[] vars = { 1, 2, 3, 4, 5, 6 };
		//
		// String s = "";
		// s = setInt(s, "1", 111);
		// s = setString(s, "2", "222");
		// s = setString(s, 3, "333");
		// s = setString(s, "4", "444");
		// s = setInt(s, 5, 555);
		// s = setInt(s, 6, vars, ",");
		// System.out.println(s);
		//
		// System.out.println(getInt(s, "1"));
		// System.out.println(getInt(s, "2"));
		// System.out.println(getInt(s, 3));
		// System.out.println(getInt(s, "4"));
		// System.out.println(getInt(s, "5"));
		// int[] vars2 = getInt(s, 6, ",");
		// for (int i : vars2) {
		// System.out.print(i + ",");
		// }
		//
		// System.out.println("-------------------");
		// Iterator it = iterator(s);
		// while (it.hasNext()) {
		// String key = (String) it.next();
		// String var = getString(s, key);
		// System.out.println(key + " = " + var);
		// }

	}
}
