package com.example.demo.cache.redis;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * 空操作缓存切面功能实现
 * 
 * @author liuyan
 * @since 1.0
 */
public class NoOpCache implements Cache {
	private String name;
	@Override
	public void clear() {
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public void evict(Object key) {
	}
	@Override
	public ValueWrapper get(Object key) {
		return null;
	}
	@Override
	public <T> T get(Object key, Class<T> type) {
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public String getName() {
		return this.name;
	}
	@Override
	public Object getNativeCache() {
		return null;
	}
	@Override
	public void put(Object key, Object value) {
	}
	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}
}
