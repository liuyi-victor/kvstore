package app_kvServer;

import java.io.*;
import java.util.concurrent.locks.*;

import org.apache.log4j.Logger;

import java.nio.channels.*;
import java.nio.file.Path;



class Entry implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6160975907797497423L;
	String key;
	String value;
	public Entry(String k, String v)
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
		if(!directory.isDirectory())
		{
			if(!directory.mkdir()) {
				logger.fatal("Storage directory does not exist and cannot be created!");
			}
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
	
	/**
	 * Input key of the entry to check if it exists as a file
	 * @param key
	 * @return File object of the entry or null if it is a directory or doesn't exist
	 */
	private synchronized File checkKeyFileExist(String key)
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
	private synchronized boolean deleteFile(String key)
	{
		try
		{
			boolean success;
			File file = checkKeyFileExist(key);
			if(file != null)
			{
				success = file.delete();
				return success;
			}
			else
				return false;
		}
		catch(Exception ex)
		{
			//TODO: ADD logging here
			return false;
		}
	}
	private synchronized int insert() {
		return -1;
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
		logger.info("Incoming Request: key = "+key+"\n value = "+value);
//		long hash = hash_function(key);
//		String hash = toASCII(key);
//		String filename = toFilename(key);
//		Path filePath = Paths.get(path, hash);
		
//		File file = new File(filename);
		
		if(value == null || value=="")
		{
			//delete operation
			/*
			boolean del;
			try {
//				FileChannel channel = raf.getChannel();
//				FileLock lock = channel.lock();
//				TODO Add some sort of lock here
				File file = checkKeyFileExist(key);
				del = file.delete(); // TODO change to different delete after structure change
//				del = Files.deleteIfExists(filePath);
			} catch (Exception e ) {
				// TODO Auto-generated catch block
				// If delete on null or failed, return delete failed
				e.printStackTrace();
				return -2;
			} finally {
//				TODO release the lock for the file
			}
			if(del) {
				return 3;
			} else {
				return -2;
			}
			*/
			if(deleteFile(key))
				return 3;
			else
				return -2;
			
		}
		else
		{
			// Insert Function
			Entry fileContent = new Entry(key,value);
			/*try {*/
				boolean isModify = true;
				File file = checkKeyFileExist(key);
				if(file == null) {
					file = new File(toFilename(key));
					isModify = false;
				}
//				OutputStream out = Files.newOutputStream(filePath);
//				ObjectOutputStream oos = new ObjectOutputStream(out);
				
//				oos.writeObject(fileContent);
				
//				RandomAccessFile raf = new RandomAccessFile(file, "rws"); // TODO determine if rw or rws is better
//				FileChannel channel = raf.getChannel();
				logger.info("File write channel opened for "+file.toString());
				try
				{
//					FileLock lock = channel.lock();
					FileOutputStream fileWrite = new FileOutputStream(file);
					ObjectOutputStream writeEntry = new ObjectOutputStream(fileWrite);
					//ObjectOutputStream writeentry = new ObjectOutputStream(output);
					try
					{
//						while(true)	// unused code for if change to different storage method
//						{
						writeEntry.writeObject(fileContent);
						logger.info(System.currentTimeMillis()+":"+"Disk PUT key="+key+" value=\""+value+"\"");
						if(isModify) {
							return 2;
						} else {
							return 1;
						}
							
//						}
					}
					catch(Exception ex)
					{
						logger.error("Cause: "+ex.getCause()+"\n Message: "+ex.getMessage()+"\n Trace: "+ex.getStackTrace());
						return -1;
					}
					finally {
//						lock.release();
						writeEntry.close();
						fileWrite.close();
//						channel.close();
//						raf.close();
						logger.info("File write channel closed for "+file.toString());
					}
				}
				catch(IOException ioex)
				{
					return -1;
				}
				
				
			/*} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}*/
			
		}
		// TODO Placeholder value
	}
	
	/**
	 * Possible function for improving readability to put write to file functionality in here
	 * @param path
	 * @param entry
	 * @return
	 */
	private boolean writeToFile(Path path, Entry entry)
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
//		try
//		{
			//File file = new File(filename);
			String filename = toFilename(key);
			File file = new File(filename);
			//				RandomAccessFile raf = new RandomAccessFile(file, "r");
//				FileChannel channel = raf.getChannel();
			try
			{
//					FileLock lock = channel.lock();
				Entry record = null;
				FileInputStream fileread = new FileInputStream(file);
				ObjectInputStream readentry = new ObjectInputStream(fileread);
				logger.info("File read channel acquired for "+file.toString());
				//ObjectOutputStream writeentry = new ObjectOutputStream(output);
				try
				{
//						while(true)	//continuously looking into the file
//						{
						record = (Entry)readentry.readObject();
						logger.info(System.currentTimeMillis()+":"+"Disk GET key="+record.key+" value=\""+record.value+"\"");
//							if(record.key == key)
//							{
//								break;
//							}
//						}
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
				finally
				{
//						lock.release();
					readentry.close();
					fileread.close();
//						channel.close();
//						raf.close();
					logger.info("File read channel released for "+file.toString());
				}
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
	
	public void clearStorage() {
		// TODO need check to ensure delete is correct
		File directory = new File(path);
		if(directory.listFiles().length < 10) {
			for(File f: directory.listFiles()) {
				if(!f.delete()) {
					logger.error("Failed to delete file "+f+" in clearStorage()");
				}
			}
			logger.error(System.currentTimeMillis()+":"+"Storage cleared.");
		}
	}
}
