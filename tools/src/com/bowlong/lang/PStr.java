package com.bowlong.lang;

import com.bowlong.objpool.StringBufPool;

public class PStr {
	public static PStr b(){
		PStr ret = new PStr();
		return ret;
	}

	public static PStr begin() {
		PStr ret = new PStr();
		return ret;
	}

	public static PStr begin(Object v) {
		String s = String.valueOf(v);
		return begin(s);
	}

	public static PStr b(Object v) {
		String s = String.valueOf(v);
		return begin(s);
	}

	public static PStr begin(String s) {
		PStr ret = begin();
		return ret.a(s);
	}

	public static PStr b(String s) {
		PStr ret = begin();
		return ret.a(s);
	}

	// ///////////////////////////////////////////////////
	public PStr() {
		sb = StringBufPool.borrowObject();
	}

	public PStr clear() {
		sb.setLength(0);
		return this;
	}

	public String end(Object v) {
		String s = String.valueOf(v);
		return end(s);
	}

	public String e(Object v) {
		String s = String.valueOf(v);
		return end(s);
	}

	public String end(String s) {
		a(s);
		return end();
	}

	public String e(String s) {
		a(s);
		return end();
	}

	public String end() {
		String ret = sb.toString();
		StringBufPool.returnObject(sb);
		sb = null;
		return ret;
	}

	public String e() {
		String ret = sb.toString();
		StringBufPool.returnObject(sb);
		sb = null;
		return ret;
	}

	// ///////////////////////////////////////////////////
	private StringBuffer sb = null;

	public PStr a(boolean v) {
		sb.append(v);
		return this;
	}

	public PStr a(byte v) {
		sb.append(v);
		return this;
	}

	public PStr a(short v) {
		sb.append(v);
		return this;
	}

	public PStr a(int v) {
		sb.append(v);
		return this;
	}

	public PStr a(long v) {
		sb.append(v);
		return this;
	}

	public PStr a(float v) {
		sb.append(v);
		return this;
	}

	public PStr a(double v) {
		sb.append(v);
		return this;
	}

	public PStr a(String v) {
		sb.append(v);
		return this;
	}

	public PStr a(StringBuffer v) {
		sb.append(v);
		return this;
	}

	public PStr a(Object v) {
		sb.append(String.valueOf(v));
		return this;
	}

	public PStr ap(String fmt, Object... args) {
		try {
			String s2 = StrEx.fmt(fmt, args);
			return a(s2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public static String str(String fmt, Object... args) {
		PStr ret = begin();
		ret.ap(fmt, args);
		return ret.end();
	}

	public PStr delLeft(int i) {
		sb.replace(0, i, "");
		return this;
	}

	public PStr delRight(int i) {
		int len = sb.length();
		sb.replace(len - i, len, "");
		return this;
	}

	public static void main(String[] args) {
		String s = PStr.begin(123).a("dddd").a(true).delRight(4).end();
		System.out.println(s);
	}
}
