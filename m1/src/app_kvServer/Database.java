package app_kvServer;

import java.io.*;
import java.util.concurrent.locks.*;
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
	private String path = "./storage/";
	public Database()
	{
		lock = new ReentrantReadWriteLock(true);
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
	public boolean checkrecordexist(String key)
	{
		if(get(key) != null)
			return true;
		else
			return false;
	}
	private File checkfileexist(String key)
	{
		long hash = hash_function(key);
		String filename = path + Long.toString(hash);
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
		long hash = hash_function(key);
		if(value == null)
		{
			//delete operation
			
		}
		else
		{
			
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
					entry record;
					FileInputStream fileread = new FileInputStream(file);
					ObjectInputStream readentry = new ObjectInputStream(fileread);
					//ObjectOutputStream writeentry = new ObjectOutputStream(output);
					try
					{
						while()	//continuously looking into the file
						{
							
						}
					}
					catch(EOFException eofex)
					{
						
					}
					lock.release();
				}
				catch(IOException ex)
				{
					
				}
			}
			else
			{
				return null;
			}
		}
		catch(FileNotFoundException notfound)
		{
			
		}
		/*
		else
		{
			return null;
		}*/
	}
}
