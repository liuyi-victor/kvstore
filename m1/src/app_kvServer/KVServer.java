package app_kvServer;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

import logger.LogSetup;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import common.messages.*;

public class KVServer implements IKVServer, Runnable {

	int serverport;
	int cache_size;
	CacheStrategy replacement;
	ServerSocket server;
	private static Logger logger = Logger.getRootLogger();
	private boolean running;
	private static Database nosql = new Database();
	
	//TODO: cache structure declaration
	//private static Cache cache = new Cache();

	/**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 * @param cacheSize specifies how many key-value pairs the server is allowed
	 *           to keep in-memory
	 * @param strategy specifies the cache replacement strategy in case the cache
	 *           is full and there is a GET- or PUT-request on a key that is
	 *           currently not contained in the cache. Options are "FIFO", "LRU",
	 *           and "LFU".
	 */
	public KVServer(int port, int cacheSize, String strategy) {
		// TODO Auto-generated method stub
		switch(strategy)
		{
			case "None":
				replacement = CacheStrategy.None;
				break;
			case "FIFO":
				replacement = CacheStrategy.FIFO;
				break;
			case "LRU":
				replacement = CacheStrategy.LRU;
				break;
			case "LFU":
				replacement = CacheStrategy.LFU;
				break;
			default:
				replacement = CacheStrategy.None;
		}
		this.cache_size = cacheSize;	
		this.serverport = port;
		initializeServer();
		//listener = new clientlistener(server);
		//listener.start();
	}

	public void run()
	{
		if(server != null) 
		{
			while(isRunning())
			{
			    try 
			    {
					Socket client = server.accept();                
					ClientConnection connection = new ClientConnection(client);
					new Thread(connection).start();
			
					logger.info("Connected to " 
							+ client.getInetAddress().getHostName() 
							+  " on port " + client.getPort());
			    } 
			    catch (IOException e) 
			    {
			    	logger.error("Error! " +
			    			"Unable to establish connection. \n", e);
			    }
	        }
        }
        logger.info("Server stopped.");
	}

	private boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean initializeServer() 
	{
		logger.info("Initialize server ...");
		try 
		{
		    this.server = new ServerSocket(this.serverport);
		    logger.info("Server listening on port: " 
		    		+ this.server.getLocalPort());    
		    return true;

		} 
		catch (IOException e) 
		{
			logger.error("Error! Cannot open server socket:");
			if(e instanceof BindException){
				logger.error("Port " + this.serverport + " is already bound!");
			}
			return false;
		}
		// setup the database (create directory/file)
	}

	@Override
	public int getPort(){
		// TODO Auto-generated method stub
		return this.serverport;
	}

	@Override
    public String getHostname(){
		// TODO Auto-generated method stub
		return server.getInetAddress().getHostName();
	}

	@Override
    public CacheStrategy getCacheStrategy(){
		return this.replacement;
	}

	@Override
    public int getCacheSize(){
		// TODO Auto-generated method stub
		return cache_size;
	}

	@Override
    public boolean inStorage(String key){
		// TODO Auto-generated method stub
		return nosql.checkrecordexist(key);
	}

	@Override
    public boolean inCache(String key){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public String getKV(String key) throws Exception{
		// TODO Auto-generated method stub
		// TODO: just get from the database for now, need to add the cache check
		
		return nosql.get(key);
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		// TODO Auto-generated method stub
		if(isCacheFull()) {
			// Based on cache strategy apply different method
			switch(this.replacement) {
				case None:
					break;
				case LRU:
					break;
				case LFU:
					break;
				case FIFO:
					break;	
			}
		}
		nosql.put(key, value);
	}

	private boolean isCacheFull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public void clearCache(){
		// TODO Auto-generated method stub
	}

	@Override
    public void clearStorage(){
		// TODO Auto-generated method stub
	}

	@Override
    public void kill(){
		// TODO Auto-generated method stub
	}

	@Override
    public void close(){
		// TODO Auto-generated method stub
	}
}