package app_kvServer;

import app_kvServer.IKVServer.CacheStrategy;

public class Storage {
	static Cache cache;
	static Database nosql;
	static int cacheSize;
	private CacheStrategy strategy;
	
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

	public String getKV() throws Exception {
		
		return "";
	}

	public void putKV() throws Exception {

	}

	public boolean inCache() {
		// TODO Auto-generated method stub
		return false;
	}
}
