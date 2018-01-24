package app_kvServer;

import java.io.*;
import java.util.concurrent.locks.*;

import org.apache.log4j.Logger;

import java.nio.channels.*;



class entry implements Serializable
{
	String key;
	String value;
	public entry(String k, String v)
	{
		key = k;
		value = v;
	}
}

public class Database 
{
	
	private ReentrantReadWriteLock lock;
	private final String path = "./storage/";
	private Logger logger = Logger.getRootLogger();
	public Database()
	{
		lock = new ReentrantReadWriteLock(true);
		File directory = new File(path);
		if(directory.isDirectory())
		{
			boolean success = directory.mkdir();
		}
	}
	private long hash_function(String name)
	{
		long hash = 5381;
		for(int i = 0; i < name.length(); i++)
	    {
			hash = ((hash << 5) + hash) + name.charAt(i);
	    }
		return hash;
		/*
		 * another hash function for testing
	    long key = 0;
	    int i;
	    for(i = 0; i < name.length(); i++)
	    {
	        key = key + (name.charAt(i) * (int)pow(2,i));
	    }
	    long index = (long)floor(fmod(hash_constant*key,1.0) * hash_length);
	    return index;
	    */
	}
	
	/** 
	 * Convert given key into ASCII and return as String
	 * @param key
	 * @return
	 */
	public String toASCII(String key) {
		StringBuilder str = new StringBuilder();
		char[] letters = key.toCharArray();
		for (char ch : letters) {
			str.append((byte)ch);
		}
		return str.toString();
	}
	
	public String toFilename(String key) {
		String hash = toASCII(key);
		String filename = path + hash;
		
//		long hash = hash_function(key);
//		String filename = path + Long.toString(hash);
		
		return filename;
	}
	
	public boolean checkrecordexist(String key)
	{
		if(get(key) != null)
			return true;
		else
			return false;
	}
	private File checkfileexist(String key)
	{
		String filename = toFilename(key);
		File file = new File(filename);
		if(file.isFile())
		{
			return file;
		}
		else
		{
			return null;
		}
	}
	/**
	 * Get a record from the database
	 *
	 * @param key
	 *            the key that identifies the record.
	 * @param value
	 *            the value of the record, null if it is a delete operation
	 * @return -2 if the delete operation failed
	 * 		   -1 if the put failed
	 * 		    1 if a record is successfully inserted
	 * 			2 if the record is successfully modified
	 * 			3 if the record is successfully deleted
	 * @throws Exception
	 *             if get operation cannot be executed (e.g. not connected to any
	 *             KV server).
	 */
	public int put(String key, String value)
	{
//		long hash = hash_function(key);
		String hash = toASCII(key);
		if(value == null)
		{
			//delete operation
			
		}
		else
		{
			logger.info(System.currentTimeMillis()+":"+"Disk PUT key="+key+" value=\""+value+"\"");
		}
		// TODO Placeholder value
		return 0;
	}
	private boolean modify()
	{
		// TODO Placeholder value
		return false;
	}
	/**
	 * Get a record from the database
	 *
	 * @param key
	 *            the key that identifies the record.
	 * @return the value identified by the given key, null if the record is not found
	 * @throws Exception
	 *             if get operation cannot be executed (e.g. not connected to any
	 *             KV server).
	 */
	public String get(String key)
	{
		// TODO should the following be changed to throw exception instead to comply with others or use the int success variable model in LFUCache.java?
		//long hash = hash_function(key);
		//String filename = path + Long.toString(hash);
		try
		{
			//File file = new File(filename);
			File file = checkfileexist(key);
			if(file != null)
			{
				RandomAccessFile raf = new RandomAccessFile(file, "r");
				FileChannel channel = raf.getChannel();
				try
				{
					FileLock lock = channel.lock();
					entry record = null;
					FileInputStream fileread = new FileInputStream(file);
					ObjectInputStream readentry = new ObjectInputStream(fileread);
					//ObjectOutputStream writeentry = new ObjectOutputStream(output);
					try
					{
						while(true)	//continuously looking into the file
						{
							record = (entry)readentry.readObject();
							if(record.key == key)
							{
								break;
							}
						}
					}
					catch(Exception ex)
					{
						if(ex instanceof EOFException)
						{
							
						}
						else if(ex instanceof ClassNotFoundException)
						{
							
						}
					}
					lock.release();
					readentry.close();
					fileread.close();
					channel.close();
					raf.close();
					if(record != null)
						return record.value;
					else 
						return null;
				}
				catch(IOException ioex)
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		catch(FileNotFoundException notfound)
		{
			return null;
		}
		/*
		else
		{
			return null;
		}*/
	}
	
	public void clearStorage() {
		// TODO need implementation
	}
}
