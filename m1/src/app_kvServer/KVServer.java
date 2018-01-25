package app_kvServer;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


import org.apache.log4j.Logger;

public class KVServer implements IKVServer, Runnable {

	int serverport;
	int cache_size;
	CacheStrategy replacement;
	ServerSocket server;
	private static Logger logger = Logger.getRootLogger();
	private boolean running;
	private static Database nosql = new Database();
	private static Cache cache; 
	
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
		// TODO Input parameter check
		this.cache_size = cacheSize;	
		if(cacheSize == 0) {
			replacement = CacheStrategy.None;
		} else {
			switch(strategy)
			{
				case "None":
					replacement = CacheStrategy.None;
					cache = null;
					break;
				case "FIFO":
					replacement = CacheStrategy.FIFO;
					cache = new FIFOCache(cacheSize);
					break;
				case "LRU":
					replacement = CacheStrategy.LRU;
					cache = new LRUCache(cacheSize);
					break;
				case "LFU":
					replacement = CacheStrategy.LFU;
					cache = new LFUCache(cacheSize);
					break;
				default:
					replacement = CacheStrategy.None;
					cache = null;
					break;
			}
		}
		
		this.serverport = port;
		initializeServer();
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
		return running;
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
		return this.serverport;
	}

	@Override
    public String getHostname(){
		return server.getInetAddress().getHostName();
	}

	@Override
    public CacheStrategy getCacheStrategy(){
		return this.replacement;
	}

	@Override
    public int getCacheSize(){
		return cache_size;
	}

	@Override
    public boolean inStorage(String key){
		return nosql.checkrecordexist(key);
	}

	@Override
    public boolean inCache(String key){
		if(cache != null) {
			return cache.inCache(key);
		} else {
			return false;
		}
	}

	@Override
    public String getKV(String key) throws Exception{
		String result = null;
		
		if(inCache(key)) {
			result = cache.get(key);
		} else {
			result = nosql.get(key);
			if(result != null && cache != null)
				cache.put(key, result);
		}
		
		return result;
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		if(cache != null) {
			cache.put(key, value);
		} else {
			// If there is no cache
			nosql.put(key, value);
//			TODO add this in cache logger.info(System.currentTimeMillis()+":"+"PUT key="+key+" value=\""+value+"\"");
		}
	}

	@Override
    public void clearCache(){
		// TODO what to do for if cache is null, is there supposed to be a message?
		if(cache != null) {
			cache.clearCache();
		}
	}

	@Override
    public void clearStorage(){
		// TODO Auto-generated method stub
		clearCache();
		nosql.clearStorage();
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