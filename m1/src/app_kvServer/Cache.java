package app_kvServer;

import java.util.*;

public interface Cache {
	public enum CacheType {
		FIFO,
		LRU,
		LFU
	}

	public int a = 0;
	//public vect
	/**
	 * Checks if key is in cache without causing access
	 * @param key
	 * @return Boolean result
	 */
	public Boolean inCache(String key);
	public String get(String key);
	public Boolean put(String key, String value);
	public Boolean flush();
	public Boolean writeback();
	public void clearCache();
}
