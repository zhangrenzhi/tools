package com.bowlong.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapBuilder {

	public Map map = new HashMap();

	public static final MapBuilder builder() {
		return new MapBuilder();
	}

	public static final MapBuilder builder(Map map) {
		MapBuilder result = new MapBuilder();
		result.map = map;
		return result;
	}

	public MapBuilder put(Object key, Object var) {
		map.put(key, var);
		return this;
	}

	public MapBuilder putAll(Map m) {
		map.putAll(m);
		return this;
	}

	public MapBuilder remove(Object key) {
		map.remove(key);
		return this;
	}

	public Map toMap() {
		return map;
	}
	
	public NewMap toNewMap(){
		return NewMap.create(map);
	}

	public List keysToList(){
		List result = new ArrayList();
		result.addAll(map.keySet());
		return result;
	}

	public List valuesToList(){
		List result = new ArrayList();
		result.addAll(map.values());
		return result;
	}

	public String toString() {
		return map.toString();
	}

	public static void main(String[] args) {
		Map m = MapBuilder.builder().put("111", "111").put(222, 222)
				.put(333, 333).toMap();
		System.out.println(m);
	}

}
