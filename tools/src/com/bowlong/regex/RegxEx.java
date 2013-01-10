package com.bowlong.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class RegxEx {
	// ������ָ���ַ���ʼ������ָ���ַ���β�����ݼ�
	public static List MatchBeginAndEnd(String str, String begin, String end) {
		Pattern p = null;
		Matcher m = null;
		List ret = new ArrayList();
		int start = 0;

		String regx = begin + "[\\s\\S]*?" + end;

		p = Pattern.compile(regx);
		m = p.matcher(str);
		while (m.find(start)) {
			String item = m.group();
			start = m.end();
			ret.add(item);
		}

		m = null;
		p = null;
		return ret;
	}

	// ������ָ���ַ���ʼ������ָ���ַ���β�����ݼ������Ұ���ָ���ַ���
	public static List MatchBeginAndEndAndInc(String str, String begin,
			String end, String inc) {
		Pattern p = null;
		Matcher m = null;
		List ret = new ArrayList();
		int start = 0;

		String regx = begin + "[\\s\\S]*" + inc + "[\\s\\S]*?" + end;
		p = Pattern.compile(regx);
		m = p.matcher(str);
		while (m.find(start)) {
			String item = m.group();
			start = m.end();
			ret.add(item);
		}

		p = null;
		m = null;
		return ret;
	}

	// ????�Ƿ�ֻ������
	public static boolean MatchNum(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean ret = false;

		String regx = "^[0-9]*$";
		p = Pattern.compile(regx);
		m = p.matcher(str);
		ret = m.matches();

		p = null;
		m = null;
		return ret;
	}

	// ֻ������nλ������
	public static boolean MatchNBitNum(String str, int n) {
		Pattern p = null;
		Matcher m = null;
		boolean ret = false;

		String regx = "^\\d{" + n + "}$";
		p = Pattern.compile(regx);
		m = p.matcher(str);
		ret = m.matches();

		p = null;
		m = null;
		return ret;
	}

	// ֻ����������nλ������
	public static boolean MatchThanNBitNum(String str, int n) {
		Pattern p = null;
		Matcher m = null;
		boolean ret = false;

		String regx = "^\\d{" + n + ",}$";
		p = Pattern.compile(regx);
		m = p.matcher(str);
		ret = m.matches();

		p = null;
		m = null;
		return ret;
	}

	// ֻ������m~nλ������
	public static boolean MatchBetweenTwoNum(String str, int n1, int n2) {
		Pattern p = null;
		Matcher m = null;
		boolean ret = false;

		String regx = "^\\d{" + n1 + "," + n2 + "}$";
		p = Pattern.compile(regx);
		m = p.matcher(str);
		ret = m.matches();

		p = null;
		m = null;
		return ret;
	}

	// ֻ��������ͷ���????����??
	public static boolean MatchNumBeginStr(String str) {
		Pattern p = null;
		Matcher m = null;
		boolean ret = false;

		String regx = "^(0|[1-9][\\s\\S]*)$";
		p = Pattern.compile(regx);
		m = p.matcher(str);
		ret = m.matches();

		p = null;
		m = null;
		return ret;
	}
	
	public static List href(String str) {
		Pattern p = null;
		Matcher m = null;
		List ret = new ArrayList();
		int start = 0;

		String regx = "href\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'>\\s]+))";

		p = Pattern.compile(regx);
		m = p.matcher(str);
		while (m.find(start)) {
			String item = m.group();
			start = m.end();
			ret.add(item);
		}
		p = null;
		m = null;
		return ret;
	}

	public static String HtmlToText(String html) {
		Pattern p = null;
		Matcher m = null;
		String ret = "";

		String regx = "<[^>]+>";
		p = Pattern.compile(regx);
		m = p.matcher(html);
		ret = m.replaceAll("");

		p = null;
		m = null;
		return ret;

	}

	public static boolean regexAnsi(String GID) {
		boolean result = false;
		String reg = null;
		Pattern p = null;
		Matcher m = null;
		try {
			reg = "^\\w+$";
			p = Pattern.compile(reg);
			m = p.matcher(GID);
			result = m.matches();
		} catch (Exception e) {
		} finally {
			reg = null;
			p = null;
			m = null;
		}
		return result;
	}
}
