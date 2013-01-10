package com.bowlong.util;

import java.util.LinkedHashMap;

@SuppressWarnings({ "rawtypes" })
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

	public LRUCache(int maxSize) {
		super(maxSize, 0.75F, true);
		maxElements = maxSize;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
		return size() > maxElements;
	}

	private static final long serialVersionUID = 1L;
	protected int maxElements;

	// ////////////////////////////////////
	// ScheduledExecutorService scheduled1Pool =
	// Executors.newScheduledThreadPool(1);
	// private ScheduledFuture<?> scheduled(Runnable r, long delay) {
	// return scheduled1Pool.schedule(r, delay, TimeUnit.MILLISECONDS);
	// }

}