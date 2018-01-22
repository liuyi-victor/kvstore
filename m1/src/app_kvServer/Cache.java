package app_kvServer;

public interface Cache {
	public enum CacheType {
		FIFO,
		LRU,
		LFU
	}

	public Boolean inCache(String key);
	public String get(String key);
	public Boolean put(String key, String value);
	public Boolean flush();
	public Boolean writeback();
}
