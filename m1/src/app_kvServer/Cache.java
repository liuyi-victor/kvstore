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
	public boolean inCache(String key);
	public String get(String key);
	public boolean put(String key, String value);
	public boolean flush();
	public void clearCache();
}
