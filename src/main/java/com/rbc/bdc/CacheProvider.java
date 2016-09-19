package com.rbc.bdc;

public interface CacheProvider {
	void put(String key, String value);
	String get(String key);
	boolean containsKey(String key);
}
