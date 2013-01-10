package com.bowlong.sql.oracle;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.bowlong.json.JSON;
import com.bowlong.objpool.StringBufPool;
import com.bowlong.pinyin.PinYin;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public abstract class BeanSupport implements ResultSetHandler, Cloneable,
		Serializable {

	public <T> T handle(ResultSet rs) throws SQLException {
		return createFor(rs);
	}

	public Map<String, Boolean> _update = new Hashtable<String, Boolean>();

	public void reset() {
		_update.clear();
	}

	public void changeIt(String str) {
		if (str == null || str.length() <= 0)
			return;
		_update.put(str, true);
	}

	public void changeIt(String str, Object oldValue, Object newValue) {
		changeIt(str);
		doEvent(str, oldValue, newValue);
	}

	public String ustr() {
		StringBuffer sb = StringBufPool.borrowObject();
		try {
			List<String> vars = new Vector<String>();
			vars.addAll(_update.keySet());
			for (String str : vars) {
				if (sb.length() > 0)
					sb.append(", ");
				sb.append("\"").append(str).append("\"");
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
			e1.printStackTrace();
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
			e.printStackTrace();
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

	public List<Object> jsonList(String fieldEn) {
		return jsonList(fieldEn, true);
	}

	public List<Object> jsonList(String fieldEn, boolean bcache) {
		Object result = null;
		if (bcache)
			result = jsonCache(fieldEn);
		else
			result = jsonNoCache(fieldEn);

		if (result != null)
			return (List<Object>) result;
		return null;
	}

	public Map jsonZhMap(String fieldZh) {
		return jsonZhMap(fieldZh, true);
	}

	public Map jsonZhMap(String fieldZh, boolean bcache) {
		String fieldEn = PinYin.getShortPinYin(fieldZh);
		return jsonMap(fieldEn, bcache);
	}

	public List<Object> jsonZhList(String fieldZh) {
		return jsonZhList(fieldZh, true);
	}

	public List<Object> jsonZhList(String fieldZh, boolean bcache) {
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
	// public Object _primaryKey() { return null;}
	// public String _tableId() { return null;}
	// public abstract <T> T insert();
	// public abstract <T> T insert2();
	// public abstract <T> T update();
	// public abstract <T> T insertOrUpdate();
	// public abstract int delete();
	// public abstract <T> T clone2();
	// public abstract boolean asyncUpdate();
}
