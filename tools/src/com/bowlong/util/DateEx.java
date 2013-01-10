package com.bowlong.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.bowlong.lang.NumEx;

public class DateEx {
	// ��ĸ ���ڻ�ʱ��Ԫ�� ��ʾ ʾ��
	// ------------------------------------------------------------------
	// G Era ��־�� Text AD
	// y �� Year 1996; 96
	// M ���е��·� Month July; Jul; 07
	// w ���е����� Number 27
	// W �·��е����� Number 2
	// D ���е����� Number 189
	// d �·��е����� Number 10
	// F �·��е����� Number 2
	// E �����е����� Text Tuesday; Tue
	// a Am/pm ��� Text PM
	// H һ���е�Сʱ����0-23�� Number 0
	// k һ���е�Сʱ����1-24�� Number 24
	// K am/pm �е�Сʱ����0-11�� Number 0
	// h am/pm �е�Сʱ����1-12�� Number 12
	// m Сʱ�еķ����� Number 30
	// s �����е����� Number 55
	// S ������ Number 978
	// z ʱ�� General time zone Pacific Standard Time;
	// PST; GMT-08:00
	// Z ʱ�� RFC 822 time zone -0800

	public static final String fmt_yyyy_MM_dd_HH_mm_ss_sss = "yyyy-MM-dd HH:mm:ss.SSS";

	public static final String fmt_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

	public static final String fmt_yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	
	public static final String fmt_MM_dd_HH_mm = "MM-dd HH:mm";

	public static final String fmt_yyyy_MM_dd = "yyyy-MM-dd";
	
	public static final String fmt_yyyy_MM = "yyyy-MM";
	
	public static final String fmt_MM_dd = "MM-dd";

	public static final String fmt_HH_mm_ss = "HH:mm:ss";

	public static final String fmt_yyyy = "yyyy";

	public static final String fmt_MM = "MM";

	public static final String fmt_dd = "dd";

	public static final String fmt_HH = "HH";

	public static final String fmt_mm = "mm";

	public static final String fmt_ss = "ss";

	public static final String fmt_SSS = "SSS";

	public static final long TIME_MILLISECOND = 1;

	public static final long TIME_SECOND = 1000 * TIME_MILLISECOND;

	public static final long TIME_MINUTE = 60 * TIME_SECOND;

	public static final long TIME_HOUR = 60 * TIME_MINUTE;

	public static final long TIME_DAY = 24 * TIME_HOUR;

	public static final long TIME_WEEK = 7 * TIME_DAY;

	public static final long TIME_YEAR = 365 * TIME_DAY;

	public static final long now() {
		return System.currentTimeMillis();
	}

	public static final String now2() {
		return now(fmt_yyyy_MM_dd_HH_mm_ss);
	}

	public static final String now3() {
		return now(fmt_yyyy_MM_dd);
	}

	public static final String now(String fmt) {
		return formatString(new Date(), fmt);
	}

	public static final Date parseDate(String v, String fmt) {
		Date dat = null;
		try {
			dat = FormatEx.getFormat(fmt).parse(v);
			//dat = new SimpleDateFormat(fmt).parse(v);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dat;
	}
	
	public static final Date parse(String v, String fmt) {
		return parseDate(v, fmt);
	}

	public static final String formatString(Date v, String fmt) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt);
		return myFmt.format(v);
	}

	public static final String format(Date v, String fmt) {
		return formatString(v, fmt);
	}

	public static final int year() {
		return year(new Date());
	}

	public static final int year(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_yyyy);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int month() {
		return month(new Date());
	}

	public static final int month(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_MM);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int day() {
		return day(new Date());
	}

	public static final int day(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_dd);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int hour() {
		return hour(new Date());
	}

	public static final int hour(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_HH);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int minute() {
		return minute(new Date());
	}

	public static final int minute(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_mm);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int second() {
		return second(new Date());
	}

	public static final int second(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_ss);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int ms() {
		return ms(new Date());
	}

	public static final int ms(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat(fmt_SSS);
		String str = myFmt.format(v);
		return NumEx.stringToInt(str);
	}

	public static final int week() {
		return week(new Date());
	}

	public static final int week(Date v) {
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(v.getTime());
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		cd = null;
		return dayOfWeek - 1;
	}

	public static final int weekInYear(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat("w");
		return NumEx.stringToInt(myFmt.format(v));
	}

	public static final int weekInYear() {
		return weekInYear(new Date());
	}

	public static final int weekInMonth(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat("W");
		return NumEx.stringToInt(myFmt.format(v));
	}

	public static final int weekInMonth() {
		return weekInMonth(new Date());
	}

	public static final int dayInYear(Date v) {
		SimpleDateFormat myFmt = FormatEx.getFormat("D");
		return NumEx.stringToInt(myFmt.format(v));
	}

	public static final int dayInYear() {
		return dayInYear(new Date());
	}

	public static final long sub(Date d1, Date d2) {
		long l1 = d1.getTime();
		long l2 = d2.getTime();
		return l1 - l2;
	}

	public static final int dayNum(int year, int month) {
		Calendar test = Calendar.getInstance();
		test.set(Calendar.YEAR, year);
		test.set(Calendar.MONTH, month);
		int totalDay = test.getActualMaximum(Calendar.DAY_OF_MONTH);
		return totalDay;
	}

	public static boolean isTimeout(long lastTm, long timeout) {
		if (timeout <= 0)
			return false;
		long l2 = System.currentTimeMillis();
		long t = l2 - lastTm;
		return (t > timeout);
	}

	public static boolean isTimeout(Date lastDat, long timeout) {
		if (timeout <= 0)
			return false;
		long LASTTIME = lastDat.getTime();
		long l2 = System.currentTimeMillis();
		long t = l2 - LASTTIME;
		return (t > timeout);
	}

	public static final String toString(long ms) {// �������������x��xʱx��x��x����
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = day < 10 ? "0" + day : "" + day;
		String strHour = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = second < 10 ? "0" + second : "" + second;
		String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
				+ milliSecond;
		strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
				+ strMilliSecond;
		return strDay + " " + strHour + ":" + strMinute + ":" + strSecond + " "
				+ strMilliSecond;
	}
}
