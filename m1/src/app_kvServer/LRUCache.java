package app_kvServer;

import java.util.*;
import java.util.PriorityQueue.*;

import app_kvServer.Cache.CacheType;

class LRUEntry implements Comparable<LRUEntry>
{
	int count;
	String key, value;
	public int compareTo(LRUEntry second) 
	{
		if(this.count < second.count)
			return -1;
		else if(this.count > second.count)
			return 1;
		else
			return 0;
	}
}
class LRUCacheLine
{
	public String value;
	public lfuentry ptr;
}
public class LRUCache implements Cache
{
	PriorityQueue<LRUEntry> queue;
	HashMap<String, LRUCacheLine> cache;
	public int size;
	CacheType type;
	public LRUCache(int size)
	{
		this.size = size;
		type = CacheType.LFU;
		queue = new PriorityQueue<LRUEntry>(size);
		cache = new HashMap<String, LRUCacheLine>(size);
	}
	public Boolean inCache(String key)
	{
			return false;
	}
	public String get(String key)
	{

			return null;
	}
	private Boolean replacement()
	{
		return false;
	}
	@Override
	public Boolean put(String key, String value) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Boolean flush() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Boolean writeback() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}
}