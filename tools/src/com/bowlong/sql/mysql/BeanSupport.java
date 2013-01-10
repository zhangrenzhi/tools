package com.bowlong.sql.mysql;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.bowlong.json.JSON;
import com.bowlong.objpool.StringBufPool;
import com.bowlong.pinyin.PinYin;
import com.bowlong.util.MapEx;
import com.bowlong.util.NewMap;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public abstract class BeanSupport implements ResultSetHandler, Cloneable,
		Serializable {

	public <T> T handle(ResultSet rs) throws SQLException {
		return createFor(rs);
	}

	private NewMap ex;

	public Map ex() {
		return ex = (ex == null) ? new NewMap() : ex;
	}

	public Object putEx(Object key, Object val) {
		return ex().put(key, val);
	}

	public boolean getExBoolean(Object key) {
		return MapEx.getBoolean(ex(), key);
	}

	public int getExInt(Object key) {
		return MapEx.getInt(ex(), key);
	}

	public long getExLong(Object key) {
		return MapEx.getLong(ex(), key);
	}

	public double getExDouble(Object key) {
		return MapEx.getDouble(ex(), key);
	}

	public String getExString(Object key) {
		return MapEx.getString(ex(), key);
	}

	private Set<String> _update = Collections
			.synchronizedSet(new HashSet<String>());

	public void reset() {
		// synchronized (_update) {
		_update.clear();
		// }
	}

	public boolean isModify() {
		return (_update != null && !_update.isEmpty());
	}

	public void changeIt(String str) {
		if (str == null || str.isEmpty())
			return;
		if (_update.contains(str))
			return;
		// synchronized (_update) {
		_update.add(str);
		// }
	}

	public void changeIt(String str, Object oldValue, Object newValue) {
		changeIt(str);
		doEvent(str, oldValue, newValue);
	}

	public String ustr() {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			List<String> vars = new Vector<String>();
			// synchronized (_update) {
			vars.addAll(_update);
			// }
			for (String str : vars) {
				if (sb.length() > 0)
					sb.append(", ");
				sb.append(str);
				sb.append("=:");
				sb.append(str);
			}
			reset();
			return sb.toString();
		} finally {
			StringBufPool.returnObject(sb);
		}
	}

	public <T> T createFor(ResultSet rs) throws SQLException {
		return null;
	}

	public Map toBasicMap() {
		return new HashMap();
	}

	public <T> T mapToObject(Map e) {
		return null;
	}

	public abstract Map toMap();

	public abstract Map toOriginalMap();

	// 当有事件的时候处理,并不强制实现
	public void doEvent(String key, Object oldValue, Object newValue) {
	};

	// public String toString() {
	// return toMap().toString();
	// }

	// 指定某字段进行JSON解析
	private Map<String, Object> jsonCache = null;

	private Object jsonCache(String fieldEn) {
		if (jsonCache == null)
			jsonCache = new HashMap<String, Object>();
		String value = valueStr(fieldEn);
		if (value == null)
			return null;

		String s = value;
		Object result = jsonCache.get(s);
		if (result != null)
			return result;

		try {
			result = JSON.parse(s);
			if (result == null)
				return null;
			if (result instanceof String)
				return null;

			jsonCache.put(fieldEn, result);
		} catch (IOException e1) {
			//e1.printStackTrace();
		}
		return result;
	}

	private Object jsonNoCache(String fieldEn) {
		String value = valueStr(fieldEn);
		if (value == null)
			return null;
		try {
			return JSON.parse(value);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

	public Map jsonMap(String fieldEn) {
		return jsonMap(fieldEn, true);
	}

	public Map jsonMap(String fieldEn, boolean bcache) {
		Object result = null;
		if (bcache)
			result = jsonCache(fieldEn);
		else
			result = jsonNoCache(fieldEn);

		if (result != null)
			return (Map) result;
		return null;
	}

	public List jsonList(String fieldEn) {
		return jsonList(fieldEn, true);
	}

	public List jsonList(String fieldEn, boolean bcache) {
		Object result = null;
		if (bcache)
			result = jsonCache(fieldEn);
		else
			result = jsonNoCache(fieldEn);

		if (result != null)
			return (List) result;
		return null;
	}

	public Map jsonZhMap(String fieldZh) {
		return jsonZhMap(fieldZh, true);
	}

	public Map jsonZhMap(String fieldZh, boolean bcache) {
		String fieldEn = PinYin.getShortPinYin(fieldZh);
		return jsonMap(fieldEn, bcache);
	}

	public List jsonZhList(String fieldZh) {
		return jsonZhList(fieldZh, true);
	}

	public List jsonZhList(String fieldZh, boolean bcache) {
		String fieldEn = PinYin.getShortPinYin(fieldZh);
		return jsonList(fieldEn, bcache);
	}

	// public abstract String valueStr(String fieldEn);
	public String valueStr(String fieldEn) {
		return null;
	};

	public String toString() {
		return toOriginalMap().toString();
	}

	// //////////////////////////////////////////////////////////
	public Object _primaryKey() {
		return null;
	}

	public String _tableId() {
		return null;
	}

	public abstract <T> T insert();

	public abstract <T> T insert2();

	public abstract <T> T update();

	public abstract <T> T insertOrUpdate();

	public abstract int delete();

	public abstract <T> T clone2();

	public abstract boolean asyncUpdate();

	// //////////////////////////////////////////////////////////

	public static final int compareTo(Object v1, Object v2) {
		if (v1 == null || v2 == null)
			return 0;

		if (v1 instanceof Boolean && v2 instanceof Boolean) {
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
		} else if (v1 instanceof Integer && v2 instanceof Integer) {
			Integer i1 = (Integer) v1;
			Integer i2 = (Integer) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof Long && v2 instanceof Long) {
			Long i1 = (Long) v1;
			Long i2 = (Long) v2;
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
		} else if (v1 instanceof Date && v2 instanceof Date) {
			Date i1 = (Date) v1;
			Date i2 = (Date) v2;
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
		} else if (v1 instanceof String && v2 instanceof String) {
			String i1 = (String) v1;
			String i2 = (String) v2;
			return i1.compareTo(i2);
		} else if (v1 instanceof java.lang.Enum && v2 instanceof java.lang.Enum) {
			java.lang.Enum i1 = (java.lang.Enum) v1;
			java.lang.Enum i2 = (java.lang.Enum) v2;
			return i1.compareTo(i2);
		}
		return 0;
	}

	public void setTimeout(ScheduledExecutorService ses, Runnable r, long delay) {
		if (ses == null || r == null)
			return;
		ses.schedule(r, delay, TimeUnit.MILLISECONDS);
	}

	public void setTimeout(ScheduledExecutorService ses, Runnable r, Date dat) {
		if (ses == null || r == null || dat == null)
			return;
		long now = System.currentTimeMillis();
		long t1 = dat.getTime();
		long delay = t1 - now;
		delay = delay < 0 ? 1 : delay;
		setTimeout(ses, r, delay);
	}
}
