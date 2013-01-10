package com.bowlong.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
public class BidiMap<K, V> extends Hashtable<K, V> {
	HashMap<V, Set<K>> valueKey = new HashMap<V, Set<K>>();

	@Override
	public synchronized V remove(Object obj) {
		V ret = super.remove(obj);
		if (ret != null) {
			if (valueKey.containsKey(ret)) {
				Set<K> keys = valueKey.get(ret);
				keys.remove(obj);
			}
		}
		return ret;
	}

	public synchronized boolean containsVKey(V valKey) {
		return valueKey.containsKey(valKey);
	}

	public synchronized K getVKey(V valKey) {
		Set<K> keys = getVKeys(valKey);
		if (keys == null || keys.isEmpty())
			return null;
		return keys.iterator().next();
	}

	public synchronized Set<K> getVKeys(V valKey) {
		if (valueKey.containsKey(valKey))
			return valueKey.get(valKey);

		return new HashSet<K>();
	}

	@Override
	public synchronized void clear() {
		super.clear();
		valueKey.clear();
	}

	@Override
	public synchronized V put(K key, V value) {
		V ret = super.put(key, value);
		if (key != null && value != null) {
			Set<K> keys = valueKey.get(value);
			if (keys == null) {
				keys = new HashSet<K>();
				valueKey.put(value, keys);
			}
			if (!keys.contains(key)) {
				keys.add(key);
			}
		}
		return ret;
	}

	public synchronized BidiMap<K, V> putPut(K key, V value) {
		put(key, value);
		return this;
	}

	@Override
	public synchronized void putAll(Map<? extends K, ? extends V> map) {
		Map.Entry entry;
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); put(
				(K) entry.getKey(), (V) entry.getValue()))
			entry = (Map.Entry) iterator.next();
	}

}
