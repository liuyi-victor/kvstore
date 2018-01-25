package app_kvServer;

import java.util.*;

import org.apache.log4j.Logger;
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
	
	Logger logger = Logger.getRootLogger();
	
	public LFUCache(int size)
	{
		this.size = size;
		type = CacheType.LFU;
		// Queue ordering based on number of access
		queue = new PriorityQueue<lfuentry>(size);
		// Actual cache implementation of each KV entry
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
	
	public String getKV(String key) {
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
	public void put(String key, String value) {
		// TODO Auto-generated method stub
		if(inCache(key)) {
			
		}
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
		queue.clear();
		logger.info(System.currentTimeMillis()+":"+"Cache CLEAR queue cleared");
		hashmap.clear();
		logger.info(System.currentTimeMillis()+":"+"Cache CLEAR hashmap cleared");
	}
}