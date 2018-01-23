package app_kvServer;

import org.apache.log4j.Logger;

import app_kvServer.IKVServer.CacheStrategy;

public class Storage {
	static Cache cache;
	static Database nosql;
	static int cacheSize;
	private Logger logger = Logger.getRootLogger();
	private CacheStrategy strategy;
	
	public Storage() {
		
	}
	
	public Storage(CacheStrategy _strategy, int _cacheSize) {
		this.strategy = _strategy;
		cacheSize = _cacheSize;
		switch(strategy) {
			case LFU:
				cache = new LFUCache(cacheSize);
			break;
			case LRU:
				cache = new LRUCache(cacheSize);
			break;
			case FIFO:
				cache = new FIFOCache(cacheSize);
			break;
			default:
				cache = null;
			break;
		}
	}
/**
 * Get KV from either cache or file system
 * @param key
 * @return The value associated with the key or null if not found
 * @throws Exception
 */
	public String getKV(String key) throws Exception {
		String result = null;
		
		if(inCache()) {
			result = cache.get(key);
		} else {
			result = nosql.get(key);
			if(result != null)
				cache.put(key, result);
		}
		
		return result;
	}

	public void putKV(String key, String value) throws Exception {
		cache.put(key, value);
		logger.info(System.currentTimeMillis()+":"+"PUT key="+key+" value=\""+value+"\"");
	}

	public boolean inCache() {
		// TODO Auto-generated method stub
		return false;
	}
}
