package app_kvServer;

public interface Cache {
	public enum CacheType {
		FIFO,
		LRU,
		LFU
	}

	public int a = 0;
	/**
	 * Checks if key is in cache without causing access
	 * @param key
	 * @return Boolean result
	 */
	public Boolean inCache(String key);
	public String get(String key);
	public void put(String key, String value);
	public Boolean flush();
	public Boolean writeback();
	public void clearCache();
}
