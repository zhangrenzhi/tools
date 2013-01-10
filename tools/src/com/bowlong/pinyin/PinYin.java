package com.bowlong.pinyin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.bowlong.objpool.StringBufPool;
import com.bowlong.util.LRUCache;

@SuppressWarnings("unchecked")
public class PinYin {

	private static PinYin py = null;
	private static Properties p = null;
	private static LRUCache<String, Object> cache = new LRUCache<String, Object>(
			128);

	private PinYin() {
		try {
			if (p == null) {
				InputStream is = getClass().getResourceAsStream("pinyin.db");
				InputStreamReader reader = new InputStreamReader(is, "UTF-8");
				p = new Properties();
				p.load(reader);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static final List<String> py(String s) {
		return getPy(s);
	}

	public static final List<String> getPy(String s) {
		if (s == null)
			return new Vector<String>();

		String _key = "__getPy" + s;
		if (cache.containsKey(_key))
			return ((List<String>) cache.get(_key));

		if (p == null || py == null)
			py = new PinYin();

		List<String> ret = new ArrayList<String>();
		int len = s.length();
		for (int n = 0; n < len; n++) {
			String k = s.substring(n, n + 1);
			String v = (String) p.get(k);
			v = v == null ? k : v;
			ret.add(v);
		}

		cache.put(_key, ret);

		return ret;
	}

	public static final List<String> shortPy(String s) {
		return getShortPy(s);
	}

	public static final List<String> getShortPy(String s) {
		if (s == null)
			return new Vector<String>();

		String _key = "__getShortPy" + s;
		if (cache.containsKey(_key))
			return ((List<String>) cache.get(_key));

		List<String> r = getPy(s);

		List<String> ret = new ArrayList<String>();
		for (String v : r) {
			String v1 = v.substring(0, 1);
			ret.add(v1);
		}

		cache.put(_key, ret);

		return ret;
	}

	public static final String upperPinYin(String s) {
		if (s == null)
			return "";

		String _key = "__upperPinYin" + s;
		if (cache.containsKey(_key))
			return (String) cache.get(_key);

		StringBuffer sb = StringBufPool.borrowObject();
		try {
			List<String> r = getPy(s);
			for (String v : r) {
				if (v != null && v.length() > 1) {
					sb.append(v.substring(0, 1).toUpperCase()
							+ v.substring(1, v.length()));
				}
			}

			String ret = sb.toString();
			cache.put(_key, ret);

			return ret;
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final String pinYin(String s) {
		return getPinYin(s);
	}

	public static final String getPinYin(String s) {
		if (s == null)
			return "";

		String _key = "__getPinYin" + s;
		if (cache.containsKey(_key))
			return (String) cache.get(_key);

		if (p == null || py == null) {
			py = new PinYin();
		}
		StringBuffer sb = StringBufPool.borrowObject();
		try {

			int len = s.length();
			for (int n = 0; n < len; n++) {
				String k = s.substring(n, n + 1);
				String v = (String) p.get(k);
				if (v == null)
					sb.append(k);
				else {
					if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ') {
						sb.append(' ');
					}
					sb.append(v);
				}
			}

			String ret = sb.toString();
			cache.put(_key, ret);
			return ret;
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public static final String upperShortPinYin(String s) {
		if (s == null)
			return "";

		String _key = "__upperShortPinYin" + s;
		if (cache.containsKey(_key))
			return (String) cache.get(_key);

		String ret = getShortPinYin(s);
		if (ret != null && ret.length() > 1) {
			ret = ret.substring(0, 1).toUpperCase()
					+ ret.substring(1, ret.length());
		}

		cache.put(_key, ret);

		return ret;
	}

	public static final String shortPinYin(String s) {
		return getShortPinYin(s);
	}

	public static final String getShortPinYin(String s) {
		if (s == null)
			return "";

		String _key = "__getShortPinYin" + s;
		if (cache.containsKey(_key))
			return (String) cache.get(_key);

		if (p == null || py == null) {
			py = new PinYin();
		}
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			int len = s.length();
			for (int n = 0; n < len; n++) {
				String k = s.substring(n, n + 1);
				String v = (String) p.get(k);
				if (v == null)
					sb.append(k);
				else {
					v = v.substring(0, 1);
					sb.append(v);
				}
			}

			String ret = sb.toString();
			cache.put(_key, ret);
			return ret;
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	/**
	 * 人民币转成大写
	 * 
	 * @param value
	 * @return String
	 */
	public static final String rmb(double value) {
		String _key = "__rmb" + value;
		if (cache.containsKey(_key))
			return (String) cache.get(_key);

		char[] hunit = { '拾', '佰', '仟' }; // 段内位置表示
		char[] vunit = { '万', '亿' }; // 段名表示
		char[] digit = { '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖' }; // 数字表示
		long midVal = (long) (value * 100); // 转化成整形
		String valStr = String.valueOf(midVal); // 转化成字符串

		String head = valStr.substring(0, valStr.length() - 2); // 取整数部分
		String rail = valStr.substring(valStr.length() - 2); // 取小数部分

		String prefix = ""; // 整数部分转化的结果
		String suffix = ""; // 小数部分转化的结果
		// 处理小数点后面的数
		if (rail.equals("00")) { // 如果小数部分为0
			suffix = "整";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "角"
					+ digit[rail.charAt(1) - '0'] + "分"; // 否则把角分转化出来
		}
		// 处理小数点前面的数
		char[] chDig = head.toCharArray(); // 把整数部分转化成字符数组
		char zero = '0'; // 标志'0'表示出现过0
		byte zeroSerNum = 0; // 连续出现0的次数
		for (int i = 0; i < chDig.length; i++) { // 循环处理每个数字
			int idx = (chDig.length - i - 1) % 4; // 取段内位置
			int vidx = (chDig.length - i - 1) / 4; // 取段位置
			if (chDig[i] == '0') { // 如果当前字符是0
				zeroSerNum++; // 连续0次数递增
				if (zero == '0') { // 标志
					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // 连续0次数清零
			if (zero != '0') { // 如果标志不为0,则加上,例如万,亿什么的
				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // 转化该数字表示
			if (idx > 0)
				prefix += hunit[idx - 1];
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // 段结束位置应该加上段名如万,亿
			}
		}

		if (prefix.length() > 0)
			prefix += '圆'; // 如果整数部分存在,则有圆的字样

		String ret = prefix + suffix;
		cache.put(_key, ret);

		return ret; // 返回正确表示
	}

	public static void main(String[] args) {
		List<String> z = PinYin.py("中文从类继承的字段");

		System.out.println(z);

		z = PinYin.shortPy("中文从类继承的字段");
		System.out.println(z);

		String s = PinYin.pinYin("中文从类继承的字段");
		System.out.println(s);

		s = PinYin.shortPinYin("中文从类继承的字段");
		System.out.println(s);

		s = PinYin.upperShortPinYin("中文从类继承的字段");
		System.out.println(s);

		s = PinYin.upperPinYin("中文从类继承的字段");
		System.out.println(s);

	}
}
