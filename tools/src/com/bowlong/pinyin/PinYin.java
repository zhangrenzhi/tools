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
	 * �����ת�ɴ�д
	 * 
	 * @param value
	 * @return String
	 */
	public static final String rmb(double value) {
		String _key = "__rmb" + value;
		if (cache.containsKey(_key))
			return (String) cache.get(_key);

		char[] hunit = { 'ʰ', '��', 'Ǫ' }; // ����λ�ñ�ʾ
		char[] vunit = { '��', '��' }; // ������ʾ
		char[] digit = { '��', 'Ҽ', '��', '��', '��', '��', '½', '��', '��', '��' }; // ���ֱ�ʾ
		long midVal = (long) (value * 100); // ת��������
		String valStr = String.valueOf(midVal); // ת�����ַ���

		String head = valStr.substring(0, valStr.length() - 2); // ȡ��������
		String rail = valStr.substring(valStr.length() - 2); // ȡС������

		String prefix = ""; // ��������ת���Ľ��
		String suffix = ""; // С������ת���Ľ��
		// ����С����������
		if (rail.equals("00")) { // ���С������Ϊ0
			suffix = "��";
		} else {
			suffix = digit[rail.charAt(0) - '0'] + "��"
					+ digit[rail.charAt(1) - '0'] + "��"; // ����ѽǷ�ת������
		}
		// ����С����ǰ�����
		char[] chDig = head.toCharArray(); // ����������ת�����ַ�����
		char zero = '0'; // ��־'0'��ʾ���ֹ�0
		byte zeroSerNum = 0; // ��������0�Ĵ���
		for (int i = 0; i < chDig.length; i++) { // ѭ������ÿ������
			int idx = (chDig.length - i - 1) % 4; // ȡ����λ��
			int vidx = (chDig.length - i - 1) / 4; // ȡ��λ��
			if (chDig[i] == '0') { // �����ǰ�ַ���0
				zeroSerNum++; // ����0��������
				if (zero == '0') { // ��־
					zero = digit[0];
				} else if (idx == 0 && vidx > 0 && zeroSerNum < 4) {
					prefix += vunit[vidx - 1];
					zero = '0';
				}
				continue;
			}
			zeroSerNum = 0; // ����0��������
			if (zero != '0') { // �����־��Ϊ0,�����,������,��ʲô��
				prefix += zero;
				zero = '0';
			}
			prefix += digit[chDig[i] - '0']; // ת�������ֱ�ʾ
			if (idx > 0)
				prefix += hunit[idx - 1];
			if (idx == 0 && vidx > 0) {
				prefix += vunit[vidx - 1]; // �ν���λ��Ӧ�ü��϶�������,��
			}
		}

		if (prefix.length() > 0)
			prefix += 'Բ'; // ����������ִ���,����Բ������

		String ret = prefix + suffix;
		cache.put(_key, ret);

		return ret; // ������ȷ��ʾ
	}

	public static void main(String[] args) {
		List<String> z = PinYin.py("���Ĵ���̳е��ֶ�");

		System.out.println(z);

		z = PinYin.shortPy("���Ĵ���̳е��ֶ�");
		System.out.println(z);

		String s = PinYin.pinYin("���Ĵ���̳е��ֶ�");
		System.out.println(s);

		s = PinYin.shortPinYin("���Ĵ���̳е��ֶ�");
		System.out.println(s);

		s = PinYin.upperShortPinYin("���Ĵ���̳е��ֶ�");
		System.out.println(s);

		s = PinYin.upperPinYin("���Ĵ���̳е��ֶ�");
		System.out.println(s);

	}
}
