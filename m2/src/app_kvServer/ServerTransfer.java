package app_kvServer;

import common.*;
import ecs.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import app_kvServer.IKVServer.CacheStrategy;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import logger.LogSetup;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class iterator
{
	private Database nosql;
	private String list[];
	private int index;
	public iterator(Database db)
	{
		this.nosql = db;
		this.list = db.allKeys();
		this.index = 0;
	}
	public String begin()
	{
		this.index = 0;
		return list[0];
	}
	public String next()
	{
		this.index++;
		return this.list[this.index];
	}
	public String end()
	{
		this.index = this.list.length - 1;
		return this.list[this.index];
	}
}
public class ServerTransfer implements Runnable
{
	String address;
	String znode;
	Socket client;
	int port;
	private ZooKeeper zk;
	private String childpath;
	
	private InputStream input;
	private OutputStream output;
	private ObjectInputStream readobj;
	private ObjectOutputStream writeobj;
	private Database nosql;
	
	// hash ranges for the new server
	private String lowerHash;
	private String upperHash;
	private static Logger logger = Logger.getRootLogger();
	
	public ServerTransfer(ECSNode node, ZooKeeper zkclient, String child, Database db)	//(String address, int port, ZooKeeper zkclient, String child, Database db)
	{
		this.address = node.address;
		this.port = node.port;
		this.lowerHash = node.lowerHash;
		this.upperHash = node.upperHash;
		this.zk = zkclient;
		this.childpath = child;
		this.nosql = db;
		initialize();
	}
	private boolean initialize()
	{
		try 
		{
		    this.client = new Socket(this.address, this.port);
		    logger.info("Connected to newly added server at: " 
		    		+ this.port); 
		    return true;
		} 
		catch (IOException e) 
		{
			logger.error("Error! Cannot open server socket:");
			if(e instanceof BindException)
			{
				logger.error("Port " + this.port + " is already bound!");
			}
			return false;
		}
	}
	private boolean hashRange(String hash)
	{
		if(this.lowerHash.compareTo(this.upperHash) < 0)
		{
			//does not wrap around the ring
			if(this.lowerHash.compareTo(hash) < 0 && this.upperHash.compareTo(hash) > 0 )
				return true;
			else 
				return false;
		}
		else
		{
			if((this.lowerHash.compareTo(hash) > 0 && this.upperHash.compareTo(hash) > 0) || (this.lowerHash.compareTo(hash) < 0 && this.upperHash.compareTo(hash) < 0))
				return true;
			else 
				return false;
		}
	}
	public void run() 
	{
		if(this.client == null)
		{
			initialize();
		}
		try 
		{
			output = client.getOutputStream();
			input = client.getInputStream();
			readobj = new ObjectInputStream(input);
			writeobj = new ObjectOutputStream(output);
			String key;
			iterator iter = new iterator(this.nosql);
			for(key = iter.begin(); key != iter.end(); key = iter.next()) 
			{
				if(hashRange(key))
				{
					try 
					{
						try
						{
							Message latestMsg = (Message)readobj.readObject();//receiveMessage();
							logger.info("RECEIVE \t<" 
									+ client.getInetAddress().getHostAddress() + ":" 
									+ client.getPort() + ">: '" 
									+ latestMsg.getStatus()+ " "+latestMsg.key+ "' "+latestMsg.value);
							// TODO
							String value = this.nosql.get(key);
							KVTransfer toclient = new KVTransfer(key, value);
							writeobj.writeObject(toclient);//sendMessage(latestMsg);
						}
						catch(ClassNotFoundException notfound)
						{
							logger.error(notfound.getMessage());
							return;
						}
					} 
					catch (IOException ioe) {
						logger.error("Error! Connection lost in run!");
						logger.error(ioe.getMessage());
					}	
				}
			}
			
		} 
		catch (IOException ioe) 
		{
			logger.error("Error! Connection could not be established!", ioe);
		} 
		finally 
		{
			
			try {
				if (client != null) 
				{
					readobj.close();
					writeobj.close();
					input.close();
					output.close();
					client.close();
				}
			} catch (IOException ioe) {
				logger.error("Error! Unable to tear down connection!", ioe);
			}
		}
		try
		{
			//zookeeper delete the child node
			zk.delete(this.childpath, -1);
		}
		catch(Exception deletenode)
		{
			logger.error(deletenode.getMessage());
		}
		
		//change the status of the server
	}
}
