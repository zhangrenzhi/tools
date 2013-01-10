package com.bowlong.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.bowlong.objpool.StringBufPool;
import com.bowlong.text.EasyTemplate;
import com.bowlong.util.StrBuilder;

public class StrEx {

	public static final StrBuilder builder() {
		return StrBuilder.builder();
	}

	public static final StrBuilder builder(StringBuffer sb) {
		return StrBuilder.builder(sb);
	}

	public static final String fix6Str(String s) {
		return String.format("%6s", s);
	}

	public static final String fixNStr(String s, int n) {
		return String.format("%" + n + "s", s);
	}

	public static final String left(String s, int len) {
		return s.substring(0, len);
	}

	public static final String right(String s, int len) {
		int length = s.length();
		return s.substring(length - len, length);
	}

	public static final String mid(String s, int begin, int end) {
		return s.substring(begin, end);
	}

	public static final String left(String s, String left) {
		int p1 = s.indexOf(left);
		p1 = p1 < 0 ? 0 : p1;
		return s.substring(0, p1);
	}

	public static final String right(String s, String right) {
		int p1 = s.lastIndexOf(right);
		p1 = p1 < 0 ? 0 : p1 + right.length();
		return s.substring(p1, s.length());
	}

	public static final String mid(String s, String begin, String end) {
		int p1 = s.indexOf(begin);
		p1 = p1 < 0 ? 0 : p1 + begin.length();
		int p2 = s.indexOf(end, p1 + begin.length());
		p2 = p2 < 0 ? s.length() : p2;
		return s.substring(p1, p2);
	}

	public static final boolean isByte(String s) {
		try {
			Byte.parseByte(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final boolean isShort(String s) {
		try {
			Short.parseShort(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final boolean isInt(String s) {
		try {
			Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final boolean isLong(String s) {
		try {
			Long.parseLong(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final boolean isFloat(String s) {
		try {
			Float.parseFloat(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final boolean isDouble(String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static final String f(String s, Object... args) {
		return String.format(s, args);
	}

	public static final String upperFirst(String s) {
		int len = s.length();
		if (len <= 0)
			return "";
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			sb.append(s.substring(0, 1).toUpperCase());
			sb.append(s.substring(1, len).toLowerCase());
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final String package2Path(String pkg) {
		return pkg.replaceAll("\\.", "/");
	}

	public static final String mapToString(Map<?, ?> m) {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			Iterator<?> it = m.keySet().iterator();
			while (it.hasNext()) {
				Object k = it.next();
				Object v = m.get(k);
				sb.append(k).append("=").append(v).append("\n");
			}
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final int cNum(String s) {
		try {
			byte[] b = s.getBytes("GBK");
			return b.length;
		} catch (Exception e) {
		}
		return 0;
	}

	public static final String toString(List<?> l) {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			Iterator<?> it = l.iterator();
			while (it.hasNext()) {
				Object v = it.next();
				String var = String.valueOf(v);
				sb.append(var).append("\n");
			}
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final byte[] toByteArray(String s, String charset)
			throws UnsupportedEncodingException {
		return s.getBytes(charset);
	}

	public static final String createString(byte[] b, String charset)
			throws UnsupportedEncodingException {
		return new String(b, charset);
	}

	public static final List<String> toLines(String s) throws IOException {
		List<String> ret = new Vector<String>();
		StringReader sr = new StringReader(s);
		BufferedReader br = new BufferedReader(sr);
		while (true) {
			String line = br.readLine();
			if (line == null)
				break;
			ret.add(line);
		}
		return ret;
	}

	// È«½Ç×Ö·û
	public static final String toW(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == ' ') {
				c[i] = '\u3000';
			} else if (c[i] < '\177') {
				c[i] = (char) (c[i] + 65248);
			}
		}
		return new String(c);
	}

	// °ë½Ç×Ö·û
	public static final String toC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}

	public static final String fmtCrLf(String fmt, Object... args) {
		return fmt(fmt, args) + "\r\n";
	}

	public static final String fmt(String fmt, Object... args) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			int length = args.length;
			for (int i = 1; i < length + 1; i++) {
				params.put(String.valueOf(i), String.valueOf(args[i - 1]));
			}
			return EasyTemplate.make(fmt, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fmt;
	}

	public static int hashCode(String s) {
		char[] value = s.toCharArray();
		return hashCode(value);
	}

	public static int hashCode(char[] value) {
		int hash = 0;
		int count = value.length;
		int offset = 0;

		int i = hash;
		if (i == 0 && count > 0) {
			int j = offset;
			char ac[] = value;
			int k = count;
			for (int l = 0; l < k; l++)
				i = 31 * i + ac[j++];

			hash = i;
		}
		return i;
	}

	public static int nextInt(List<Integer> list) {
		int index = NumEx.nextInt(list.size());
		return list.get(index);
	}

	public static String removeLeft(String s, int n) {
		int len = s.length();
		if (len < n)
			return "";
		return s.substring(n, len - n);
	}

	public static String removeRight(String s, int n) {
		int len = s.length();
		if (len < n)
			return "";
		return s.substring(0, len - n - 1);
	}

	public static void main(String[] args) {
		System.out.println(removeLeft("ÄãºÃ:${2}, ÎÒoo${1}", 3));
		System.out.println(removeRight("ÄãºÃ:${2}, ÎÒoo${1}", 3));
	}

	public static boolean isEmpty(String type) {
		return type == null || type.isEmpty();
	}
}
