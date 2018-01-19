package app_kvServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Database 
{
	private long hash_function(String name)
	{
		long hash = 5381;
		for(int i = 0; i < name.length(); i++)
	    {
			hash = ((hash << 5) + hash) + name.charAt(i);
	    }
		return hash;
		/*
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
	public boolean put(String key, String value)
	{
		if(value == null)
		{
			//delete operation
			
		}
		else
		{
			
		}
	}
	private synchronized boolean modify()
	{
		
	}
	public String get(String key)
	{
		hash_function(key);
		
	}
}
