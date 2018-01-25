package app_kvServer;

import java.util.*;
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
	HashMap<String, cacheline> hashmap;
	public int size;
	CacheType type;
	public LFUCache(int size)
	{
		this.size = size;
		type = CacheType.LFU;
		queue = new PriorityQueue<lfuentry>(size);
		hashmap = new HashMap<String, cacheline>(size);
	}
	public Boolean inCache(String key)
	{
		if(hashmap.get(key) != null)
			return true;
		else
			return false;
	}
	
	public String get(String key)
	{
		cacheline entry = hashmap.get(key);
		if(entry != null)
		{
			// cache hit
			if(queue.remove(entry.ptr))
			{
				entry.ptr.count = entry.ptr.count + 1; // TODO what is this?
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
		if(hashmap.remove(entry.key).value == entry.value)
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
		queue.clear();
		queue.removeAll(queue);
		hashmap.clear();
	}
}