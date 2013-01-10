package com.bowlong.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BeanEvent<T> {
	boolean asyn = false;
	Map<String, Boolean> keys = new HashMap<String, Boolean>();

	public BeanEvent(String key) {
		this.asyn = true;
		keys.put(key, true);
	}

	public BeanEvent(String key, boolean asyn) {
		this.asyn = asyn;
		keys.put(key, true);
	}

	public BeanEvent(List<String> keys) {
		this(keys, true);
	}
	
	public BeanEvent(List<String> keys, boolean asyn) {
		this.asyn = asyn;
		for (String key : keys) {
			this.keys.put(key, true);
		}
	}

	public boolean containsKey(String key) {
		return this.keys.containsKey(key);
	}
	
	public void doEvent(final T obj, final String key, final Object oldValue, final Object newValue){
		if(!containsKey(key))
			return;
		
		if(asyn){ // 异步执行
			execute(new Runnable() {
				public void run() {
					onEvent(obj, key, oldValue, newValue);
				}
			});
		}else{ // 同步执行
			onEvent(obj, key, oldValue, newValue);
		}
	}

	public abstract void onEvent(T obj, String key, Object oldValue, Object newValue);
	
	
	private static ExecutorService _threadPool = null;

	public static final void execute(Runnable r) {
		if (_threadPool == null)
			_threadPool = Executors.newCachedThreadPool();
		_threadPool.execute(r);
	}

}
