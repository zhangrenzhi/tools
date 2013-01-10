package com.bowlong.sql;

public enum CacheModel {
	NO_CACHE, // 不缓存
	FULL_CACHE, // 全缓存
	HOT_CACHE, // 热缓存 (未使用)
	FULL_MEMORY, // 全内存
	STATIC_CACHE // 静态缓存(数据不增长)
}
