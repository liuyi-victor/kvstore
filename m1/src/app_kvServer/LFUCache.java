package app_kvServer;

import java.util.*;
import java.util.PriorityQueue.*;

import app_kvServer.Cache.CacheType;

class lfuentry implements Comparable<lfuentry>
{
	int count;
	String key, value;
	public int compareTo(lfuentry second) 
	{
		if(this.count < second.count)
			return -1;
		else if(this.count > second.count)
			return 1;
		else
			return 0;
	}
}
class cacheline
{
	public String value;
	public lfuentry ptr;
}
public class LFUCache implements Cache
{
	PriorityQueue<lfuentry> queue;
	HashMap<String, cacheline> cache;
	public int size;
	CacheType type;
	public LFUCache(int size)
	{
		this.size = size;
		type = CacheType.LFU;
		queue = new PriorityQueue<lfuentry>(size);
		cache = new HashMap<String, cacheline>(size);
	}
	public Boolean inCache(String key)
	{
		if(cache.get(key) != null)
			return true;
		else
			return false;
	}
	public String get(String key)
	{
		cacheline entry = cache.get(key);
		if(entry != null)
		{
			if(queue.remove(entry.ptr))
			{
				entry.ptr.count++;
				queue.offer(entry.ptr);
			}
			return entry.value;
		}
		else
			return null;
	}
	private Boolean replacement()
	{
		lfuentry entry = queue.poll();
		if(cache.remove(entry.key).value == entry.value)
		{
			//success
			return true;
		}
		else
		{
			//error occurred
			queue.offer(entry);	//add the entry back
			return false;
		}
	}
}