/*
 * LRU cache referenced code by StackOverflow user 'liarspocker' and 'Ciro Santilli'
 * https://stackoverflow.com/questions/23772102/lru-cache-in-java-with-generics-and-o1-operations
 */

package app_kvServer;

import java.util.*;
import java.util.PriorityQueue.*;

import org.apache.log4j.Logger;

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

class Node<T, U> {
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

public class LRUCache implements Cache
{
	PriorityQueue<LRUEntry> queue;
//	HashMap<String, Node<String, String>> cache;
	LinkedHashMap<String,String> cache;
	Node<String, String> leastRecentlyUsed = new Node<String, String>(null,null,null,null);
	Node<String, String> mostRecentlyUsed = leastRecentlyUsed;
	
	Logger logger = Logger.getRootLogger();
	
	public int maxSize;
	CacheType type;
	public LRUCache(int size)
	{
		this.maxSize = size;
		type = CacheType.LFU;
		queue = new PriorityQueue<LRUEntry>(size);
//		cache = new HashMap<String, Node<String, String>>(size);
		cache = new LinkedHashMap<String, String>(){
			@Override
			protected boolean removeEldestEntry(Map.Entry<String,String> eldest) {
				return size() > maxSize ;
				
			}
		};
	}
	
	public boolean inCache(String key)
	{
		return cache.containsKey(key);
	}
	
	public String get(String key)
	{
		String result = cache.get(key);
		String value;
		if(result != null) {
			// Refresh access of hashmap
			value = cache.remove(key);
			cache.put(key, value);
		}
		
		// TODO possibly add the call to db here
		return result;
	}
	
	@Override
	public void put(String key, String value) {
        if (cache.containsKey(key)) {
        		cache.remove(key);
        }
        cache.put(key, value);
	}
	
	@Override
	public boolean writeback() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void clearCache() {
		
	}
}