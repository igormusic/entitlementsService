package com.rbc.bdc;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class LocalCacheProvider implements CacheProvider {

	private static ConcurrentHashMap<String,String> map = new ConcurrentHashMap<String,String>();
	@Override
	public void put(String key, String value) {
		// TODO Auto-generated method stub
		if(!map.containsKey(key))
		{
			map.put(key, value);
		}
		else
		{
			map.replace(key, value);
		}
	}

	@Override
	public String get(String key) {
		// TODO Auto-generated method stub
		return map.get(key);
	}

	@Override
	public boolean containsKey(String key) {
		// TODO Auto-generated method stub
		return map.containsKey(key);
	}

}
