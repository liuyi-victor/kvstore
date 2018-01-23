package app_kvServer;

import java.util.*;
import java.util.PriorityQueue.*;

import app_kvServer.Cache.CacheType;

class FIFOEntry implements Comparable<FIFOEntry>
{
	int count;
	String key, value;
	public int compareTo(FIFOEntry second) 
	{
		if(this.count < second.count)
			return -1;
		else if(this.count > second.count)
			return 1;
		else
			return 0;
	}
}
class FIFOCacheLine
{
	public String value;
	public FIFOEntry ptr;
}
public class FIFOCache implements Cache
{
	PriorityQueue<FIFOEntry> queue;
	HashMap<String, FIFOCacheLine> cache;
	public int size;
	CacheType type;
	public FIFOCache(int size)
	{
		this.size = size;
		type = CacheType.FIFO;
		queue = new PriorityQueue<FIFOEntry>(size);
		cache = new HashMap<String, FIFOCacheLine>(size);
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