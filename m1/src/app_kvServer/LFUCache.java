package app_kvServer;

import java.util.*;
import org.apache.log4j.Logger;

class Node<T, U> 
{
    Node<T, U> previous;
    Node<T, U> next;
    T key;
    U value;

    public Node(Node<T, U> previous, Node<T, U> next, T key, U value){
        this.previous = previous;
        this.next = next;
        this.key = key;
        this.value = value;
    }
}
class FIFOEntry
{
	String key;
	String value;
	FIFOEntry() { }
	FIFOEntry(String key, String value)
	{
		this.key = key;
		this.value = value;
	}
}
class lfuentry implements Comparable<lfuentry>
{
	int count;
	String key, value;
	public lfuentry() { }
	public lfuentry(String key, String value, int count)
	{
		this.key = key;
		this.value = value;
		this.count = count;
	}
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
	// data structures to implement the different types of caches
	// 1. for FIFO cache
	ArrayList<FIFOEntry> fifo;
	
	// 2. for LFU cache
	PriorityQueue<lfuentry> queue;
	HashMap<String, cacheline> hashmap;
	
	// 3. for LRU cache
	LinkedHashMap<String,String> cache;
	Node<String, String> leastRecentlyUsed;
	Node<String, String> mostRecentlyUsed;
	
	// the cache will be delegating the NoSQL database operations
	static private Database nosql = new Database();
	
	//public int size;
	int capacity;
	
	IKVServer.CacheStrategy type;
	Logger logger = Logger.getRootLogger();
	
	public LFUCache()
	{
		// this.capacity = size;
		
		// Queue ordering based on number of access
		// queue = new PriorityQueue<lfuentry>(size);
		// Actual cache implementation of each KV entry
		// hashmap = new HashMap<String, cacheline>(size);
	}
	public void setType(int size, IKVServer.CacheStrategy strategy)
	{
		this.capacity = size;
		type = strategy;
		switch(strategy) 
		{
			case LFU:
				// Queue ordering based on number of access
				queue = new PriorityQueue<lfuentry>(size);
				// Actual cache implementation of each KV entry
				hashmap = new HashMap<String, cacheline>(size);
				break;
			case LRU:
				Node<String, String> leastRecentlyUsed = new Node<String, String>(null,null,null,null);
				Node<String, String> mostRecentlyUsed = leastRecentlyUsed;
				cache = new LinkedHashMap<String, String>(){
					@Override
					protected boolean removeEldestEntry(Map.Entry<String,String> eldest) {
						return size() > capacity ;
						
					}
				};
				break;
			case FIFO:
				 fifo = new ArrayList<FIFOEntry>(capacity);
				break;
		}
	}
	public boolean inCache(String key)
	{
		switch(type) 
		{
			case LFU:
				if(hashmap.get(key) != null)
					return true;
				else
					return false;
			case LRU:
				return cache.containsKey(key);
			case FIFO:
				FIFOEntry entry;
				for(int i = 0; i < fifo.size(); i++)
				{
					entry = fifo.get(i);
					if(entry.key == key)
						return true;
				}
				return false;
			default:
				return false;
		}
		//return hashmap.get(key);
	}
//	private boolean searchInCache(String key)
//	{
//		
//	}
	public synchronized String get(String key)
	{
		if(type == IKVServer.CacheStrategy.LFU)
		{
				cacheline entry = hashmap.get(key);
				if(entry != null)
				{
					if(queue.remove(entry.ptr))
					{
						entry.ptr.count = entry.ptr.count + 1;
						queue.offer(entry.ptr);
					}
					return entry.value;
				}
				else
				{
					// try getting the record from the database
					String value = nosql.get(key);
					if(value == null)
						return null;
					else
					{
						// the database contains the record and save it to the cache
						cacheline insertion = new cacheline();
						lfuentry element = new lfuentry(key, value, 1);
						
						insertion.value = value;
						insertion.ptr = element;
						
						if(hashmap.size() >= this.capacity)
						{
							replacement();
						}
						hashmap.put(key, insertion);
						queue.offer(element);
						return value;
					}
				}
		}
		else if(type == IKVServer.CacheStrategy.LRU)
		{
			String result = cache.get(key);
			String value;
			if(result != null) 
			{
				// Refresh access of hashmap
				value = cache.remove(key);
				cache.put(key, value);
				return result;
			}
			else
			{
				// the cache does not have the records, try searching in the database
				value = nosql.get(key);
				if(value == null)
					return null;
				else
				{
					// the database contains the record and save it to the cache
					// TODO: insert into the LRU cache
				}
			}
			return result;	//TODO: placeholder for now
		}
		else
		{
				FIFOEntry entry;
				for(int i = 0; i < fifo.size(); i++)
				{
					entry = fifo.get(i);
					if(entry.key == key)
						return entry.value;
				}
				String value = nosql.get(key);
				if(value == null)
					return null;
				else
				{
					// the database contains the record and save it to the cache
					FIFOEntry insertion = new FIFOEntry(key, value);
					if(hashmap.size() >= this.capacity)
					{
						replacement();
					}
					fifo.add(insertion);
					return value;
				}
		}
		
//		cacheline entry = hashmap.get(key);
//		if(entry != null)
//		{
//			// cache hit
//			if(queue.remove(entry.ptr))
//			{
//				entry.ptr.count = entry.ptr.count + 1; // TODO what is this?
//				queue.offer(entry.ptr);
//			}
//			return entry.value;
//		}
//		else
//			return null;
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
	
	private boolean replacement()
	{
		if(this.type == IKVServer.CacheStrategy.LFU)
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
		else if(this.type == IKVServer.CacheStrategy.LRU)
		{
			
		}
		else
		{
			
		}
	}
	@Override
	public synchronized int put(String key, String value) {
		// TODO Auto-generated method stub
		if(this.type == IKVServer.CacheStrategy.LFU)
		{
			cacheline entry = hashmap.get(key);
			int ret;
			if(value == null || value.isEmpty())
			{
				//delete
				//Database delete first
				ret = nosql.put(key, null);
				if(ret != 3)
					return ret;
				if(entry != null)
				{
					queue.remove(entry.ptr);
				}
				hashmap.remove(key);
			}
			else
			{
				ret = nosql.put(key, value);
				if(entry != null)
				{
					//cannot be an insert
					if(queue.remove(entry.ptr))
					{
						entry.ptr.count = entry.ptr.count + 1;
						entry.value = value;
						queue.offer(entry.ptr);
					}
				}
				else
				{
				}
			}
		}
		else if(type == IKVServer.CacheStrategy.LRU)
		{
			
		}
		else
		{
			
		}
	}

	@Override
	public void clearCache() {
		queue.clear();
		logger.info(System.currentTimeMillis()+":"+"Cache CLEAR queue cleared");
		hashmap.clear();
		logger.info(System.currentTimeMillis()+":"+"Cache CLEAR hashmap cleared");
	}
}