package com.bowlong.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import com.bowlong.objpool.StringBufPool;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListEx {
	private static Random rnd;
	static {
		rnd = new Random(System.currentTimeMillis());
	}

	public static final ListBuilder builder() {
		return ListBuilder.builder();
	}

	public static final ListBuilder builder(List list) {
		return ListBuilder.builder(list);
	}

	public static final List newList() {
		return new Vector();
	}

	public static final List newArrayList() {
		return new ArrayList();
	}

	public static final List newLinkedList() {
		return new LinkedList();
	}

	public static final List newVector() {
		return new Vector();
	}

	public static final <T> T get(List list, int index) {
		return (T) list.get(index);
	}

	public static final List toList(String s) {
		List l = new Vector();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		try {
			while (true) {
				String v = br.readLine();
				if (v == null)
					break;
				l.add(v);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return l;
	}

	public static final List toList(Set m) {
		List ret = newList();
		ret.addAll(m);
		return ret;
	}

	public static final List<Integer> toList(int[] array) {
		List ret = newList();
		for (int v : array) {
			ret.add(v);
		}
		return ret;
	}

	public static final List<String> toList(String[] array) {
		List ret = newList();
		for (String v : array) {
			ret.add(v);
		}
		return ret;
	}

	public static final List listIt(Object... var) {
		List result = new Vector();
		for (Object e : var) {
			result.add(e);
		}
		return result;
	}

	public static final boolean isEmpty(List l){
		return (l == null || l.isEmpty());
	}

	public static final boolean notEmpty(List l){
		return (l != null && !l.isEmpty());
	}

	public static final int[] toIntArray(List<Integer> list) {
		if (list == null || list.isEmpty())
			return new int[0];

		int count = list.size();
		int[] result = new int[count];

		for (int i = 0; i < count; i++) {
			Integer v = list.get(i);
			if (v == null)
				result[i] = 0;
			result[i] = v.intValue();
		}
		return result;
	}

	public static final int[] toIntArray(int... args) {
		return args;
	}

	public static final boolean withIn(int[] array, int v) {
		for (int i : array) {
			if (v == i)
				return true;
		}
		return false;
	}

	public static boolean withIn(Set<Integer> sets, int[] array) {
		for (int i : array) {
			if (sets.contains(i))
				return true;
		}
		return false;
	}

	public static boolean withIn(int[] array, Set<Integer> sets) {
		Iterator<Integer> it = sets.iterator();
		while (it.hasNext()) {
			int i = it.next();
			if (withIn(array, i))
				return true;
		}
		return false;
	}

	public static final int max(List<Integer> list) {
		return Collections.max(list);
	}

	public static final int min(List<Integer> list) {
		return Collections.max(list);
	}

	// 是否有交集
	public static final boolean disjoint(List l1, List l2) {
		return Collections.disjoint(l1, l2);
	}

	// 求交集
	public static final List intersected(List l1, List l2) {
		List result = new Vector();
		result.addAll(l1);
		result.retainAll(l2);
		return result;
	}

	public static final String[] toStrArray(List<String> list) {
		if (list == null || list.isEmpty())
			return new String[0];

		int count = list.size();
		String[] result = new String[count];

		for (int i = 0; i < count; i++) {
			String v = list.get(i);
			if (v == null)
				result[i] = "";
			result[i] = v;
		}
		return result;
	}

	public static final List toArrayList(List list) {
		List ret = newArrayList();
		for (Object e : list) {
			ret.add(e);
		}
		return ret;
	}

	public static final List toLinkedList(List list) {
		List ret = newLinkedList();
		for (Object e : list) {
			ret.add(e);
		}
		return ret;
	}

	public static final List toVector(List list) {
		List ret = newVector();
		for (Object e : list) {
			ret.add(e);
		}
		return ret;
	}

	public static final List toArrayList(Object[] array) {
		if (array == null)
			return null;
		List list = newArrayList();
		for (Object e : array)
			list.add(e);
		return list;
	}

	public static final List toLinkedList(Object[] array) {
		if (array == null)
			return null;
		List list = newLinkedList();
		for (Object e : array)
			list.add(e);
		return list;
	}

	public static List keyToList(Map map) {
		List list = ListEx.newList();
		list.addAll(map.keySet());
		return list;
	}

	public static List valueToList(Map map) {
		List list = ListEx.newList();
		list.addAll(map.values());
		return list;
	}

	public static final List toVector(Object[] array) {
		if (array == null)
			return null;
		List list = newVector();
		for (Object e : array)
			list.add(e);
		return list;
	}

	public static final List copy(List src) {
		List dest = newVector();
		Collections.copy(dest, src);
		return dest;
	}

	public static final List reverse(List src) {
		Collections.reverse(src);
		return src;
	}

	public static final List rotate(List src, int distance) {
		Collections.rotate(src, distance);
		return src;
	}

	public static final List shuffle(List src) {
		Collections.shuffle(src);
		return src;
	}

	public static final List shuffleRnd(List src) {
		Collections.shuffle(src, rnd);
		return src;
	}

	public static final List sort(List src) {
		Collections.sort(src);
		return src;
	}

	public static final List<Map> sort(List m1, final String key) {
		Collections.sort(m1, new Comparator<Map>() {
			public int compare(Map o1, Map o2) {
				Object v1 = o1.get(key);
				Object v2 = o2.get(key);
				if (v1 == null || v2 == null)
					return 0;
				return compareTo(v1, v2);
			}
		});
		return m1;
	}

	public static final int compareTo(Object v1, Object v2) {
		if (v1 == null || v2 == null)
			return 0;

		if (v1 instanceof Integer && v2 instanceof Integer) {
			Integer i1 = (Integer) v1;
			Integer i2 = (Integer) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Long && v2 instanceof Long) {
			Long i1 = (Long) v1;
			Long i2 = (Long) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Date && v2 instanceof Date) {
			Date i1 = (Date) v1;
			Date i2 = (Date) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof String && v2 instanceof String) {
			String i1 = (String) v1;
			String i2 = (String) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Byte && v2 instanceof Byte) {
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
		} else if (v1 instanceof java.lang.Enum && v2 instanceof java.lang.Enum) {
			java.lang.Enum i1 = (java.lang.Enum) v1;
			java.lang.Enum i2 = (java.lang.Enum) v2;
			return i1.compareTo(i2);
		}
		return 0;
	}

	public static final List swap(List list, int i, int j) {
		Collections.swap(list, i, j);
		return list;
	}

	public static final List sort2(List src, Comparator comparator) {
		Collections.sort(src, comparator);
		return src;
	}

	public static final List<Map> sortIntMap(List<Map> m1, final Object key) {
		Collections.sort(m1, new Comparator<Map>() {
			public int compare(Map o1, Map o2) {
				int i1 = (Integer) o1.get(key);
				int i2 = (Integer) o2.get(key);
				return i1 - i2;
			}
		});
		return m1;
	}

	public static final List<Map> sortLongMap(List<Map> m1, final Object key) {
		Collections.sort(m1, new Comparator<Map>() {
			public int compare(Map o1, Map o2) {
				long i1 = (Long) o1.get(key);
				long i2 = (Long) o2.get(key);
				return i1 > i2 ? 1 : -1;
			}
		});
		return m1;
	}

	public static final List<Byte> distinctByte(List<Byte> vars) {
		List<Byte> ret = new Vector<Byte>();
		Map<Byte, Byte> mvars = new Hashtable<Byte, Byte>();
		for (Byte v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<Short> distinctShort(List<Short> vars) {
		List<Short> ret = new Vector<Short>();
		Map<Short, Short> mvars = new Hashtable<Short, Short>();
		for (Short v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<Integer> distinctInteger(List<Integer> vars) {
		List<Integer> ret = new Vector<Integer>();
		Map<Integer, Integer> mvars = new Hashtable<Integer, Integer>();
		for (Integer v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<Long> distinctLong(List<Long> vars) {
		List<Long> ret = new Vector<Long>();
		Map<Long, Long> mvars = new Hashtable<Long, Long>();
		for (Long v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final List<String> distinctString(List<String> vars) {
		List<String> ret = new Vector<String>();
		Map<String, String> mvars = new Hashtable<String, String>();
		for (String v : vars) {
			mvars.put(v, v);
		}
		ret.addAll(mvars.values());
		return ret;
	}

	public static final String formatString(List l) {
		int depth = 1;
		return formatString(l, depth);
	}

	public static final String formatString(List l, int depth) {
		int p = 0;
		int size = l.size();
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append("[\n");
			for (Object v : l) {
				for (int i = 0; i < depth; i++)
					sb.append("    ");
				if (v instanceof List) {
					sb.append(ListEx.formatString((List) v, depth + 1));
				} else if (v instanceof Map) {
					sb.append(MapEx.formatString((Map) v, depth + 1));
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
			sb.append("]");
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	// //////////////////////////////////////////////////

	public static void main(String[] args) {
	}
}
