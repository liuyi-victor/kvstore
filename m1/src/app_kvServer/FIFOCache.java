package app_kvServer;

import java.util.*;
import java.util.PriorityQueue.*;

import app_kvServer.Cache.CacheType;

class FIFOEntry
{
	String key;
	String value;
}
public class FIFOCache implements Cache
{
	ArrayList<FIFOEntry> queue;
	//HashMap<String, FIFOCacheLine> cache;
	public int size;
	CacheType type;
	public FIFOCache(int size)
	{
		this.size = size;
		type = CacheType.FIFO;
		queue = new ArrayList<FIFOEntry>(size);
		//cache = new HashMap<String, FIFOCacheLine>(size);
	}
	public boolean inCache(String key)
	{
			if(get(key) != null)
				return true;
			else
				return false;
	}
	public String get(String key)
	{
		FIFOEntry entry;
		for(int i = 0; i < queue.size(); i++)
		{
			entry = queue.get(i);
			if(entry.key == key)
				return entry.value;
		}
		return null;
	}
	private boolean replacement()
	{
		if(queue.size() > 0)
		{
			FIFOEntry evicted = queue.remove(0);		
			return true;
		}
		else
			return false;
	}
	@Override
	public boolean put(String key, String value) {
		// TODO Auto-generated method stub
		FIFOEntry entry;
		for(int i = 0; i < queue.size(); i++)
		{
			entry = queue.get(i);
			if(entry.key == key)
			{
				if(value != null)
					entry.value = value;
				else
					queue.remove(i);
				return true;
			}
		}
		return false;
	}
	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		queue.clear();
	}
}