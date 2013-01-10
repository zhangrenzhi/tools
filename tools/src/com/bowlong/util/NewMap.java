package com.bowlong.util;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({ "serial", "rawtypes", "unchecked" })
public class NewMap<K, V> extends ConcurrentHashMap<K, V> {
	public static NewMap create() {
		return new NewMap();
	}

	public static NewMap newly() {
		return new NewMap();
	}

	public static NewMap create(Map map) {
		if (map instanceof NewMap)
			return (NewMap) map;

		NewMap ret = new NewMap();
		ret.putAll(map);
		return ret;
	}

	public static NewMap newly(Map map) {
		return create(map);
	}

	public static NewMap newly(Object key, Object value) {
		return create().putPut(key, value);
	}

	public NewMap<K, V> putPut(K key, V value) {
		super.put(key, value);
		return this;
	}

	public NewMap<K, V> put2(K key, V value) {
		return putPut(key, value);
	}

	public NewMap<K, V> putPut(Map map) {
		super.putAll(map);
		return this;
	}

	public NewMap<K, V> put2(Map map) {
		return putPut(map);
	}

	public boolean getBoolean(K key) {
		return MapEx.getBoolean(this, key);
	}

	public byte getByte(K key) {
		return MapEx.getByte(this, key);
	}

	public short getShort(K key) {
		return MapEx.getShort(this, key);
	}

	public int getInt(K key) {
		return MapEx.getInt(this, key);
	}

	public long getLong(K key) {
		return MapEx.getLong(this, key);
	}

	public float getFloat(K key) {
		return MapEx.getFloat(this, key);
	}

	public double getDouble(K key) {
		return MapEx.getDouble(this, key);
	}

	public String getString(K key) {
		return MapEx.getString(this, key);
	}

	public Date getDate(K key) {
		return MapEx.getDate(this, key);
	}

	public NewDate getNewDate(K key) {
		return MapEx.getNewDate(this, key);
	}

	public Map getMap(K key) {
		return MapEx.getMap(this, key);
	}

	public NewMap getNewMap(K key) {
		return MapEx.getNewMap(this, key);
	}

	public List getList(K key) {
		return MapEx.getList(this, key);
	}

	public NewList getNewList(K key) {
		return MapEx.getNewList(this, key);
	}

	public Set getNewSet(K key) {
		return MapEx.getNewSet(this, key);
	}

	public Set getSet(K key) {
		return MapEx.getSet(this, key);
	}

	public Object getKey() {
		try {
			if (isEmpty())
				return "";
			return MapEx.getKey(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public Object getValue() {
		try {
			if (isEmpty())
				return "";
			return MapEx.getValue(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public Map<K, V> synchronizedMap() {
		return Collections.synchronizedMap(this);
	}

	public Map toMap() {
		return this;
	}
}
