package app_kvServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import app_kvServer.IKVServer.CacheStrategy;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import java.util.*;
import common.*;
import logger.LogSetup;
import ecs.*;

public class KVServer implements IKVServer, Runnable, Watcher, StatCallback//, DataMonitor.DataMonitorListener {
{
	int serverport;
	int cache_size;
	CacheStrategy replacement;
	ServerSocket server;

	public enum ServerState
	{
		SERVER_STOPPED,         /* Server is stopped, no requests are processed */
		SERVER_WRITE_LOCK,
		SERVER_RUNNING
	};
	
	private final int wellknowports = 1024;
	private static Logger logger = Logger.getRootLogger();
	private ServerState running;
	private static Database nosql = new Database();
	private static Cache cache = new Cache(); 
	
	private ECSNode meta;
	private List<String> child;
	private ZooKeeper zk;
	private Watcher watch;
	private String zkhost;
	private String znode;
	private int zkport;
	/**
	 * Start KV Server with selected name
	 * @param name			unique name of server
	 * @param zkHostname	hostname where zookeeper is running
	 * @param zkPort		port where zookeeper is running
	 */
	public KVServer(String name, String zkHostname, int zkPort) 
	{
		// TODO Auto-generated method stub
		zkhost = zkHostname;
		zkport = zkPort;
		this.znode = "/"+name;
		
		//initiate a zookeeper client at this KVServer
		try 
		{
			zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
			//zk.exists(name, true, this, null);
			Stat stat = zk.exists(name, this);
			child = zk.getChildren(name, this, stat);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		//initialize the state to SERVER_STOPPED
		this.running = ServerState.SERVER_STOPPED;
	}

	public boolean initKVServer(String metadata, int cacheSize, String replacementStrategy) 
	{
		try 
		{
			this.update(metadata);
			
			
		} 
		catch (Exception e) {
			logger.error("Metadata could not be updated", e);
			return false;
		}
		this.cache_size = cacheSize;
		switch(replacementStrategy)
		{
			case "FIFO":
				replacement = CacheStrategy.FIFO;
				break;
			case "LRU":
				replacement = CacheStrategy.LRU;
				//cache = new LRUCache(cacheSize);
				break;
			case "LFU":
				replacement = CacheStrategy.LFU;
				//cache = new LFUCache(cacheSize);
				break;
			default:
				System.out.println("Error! Invalid argument <strategy>! Not a valid caching strategy. Available options are FIFO | LRU | LFU !");
				System.out.println("Usage: Server <port> <cache_size> <strategy>!");
				System.exit(-1);
				//break;
		}
	}
	private void update(String metadata)
	{
		
	}
	public void process(WatchedEvent event)
	{
		String path = event.getPath();
        if (event.getType() == Event.EventType.None) 
        {
            // We are are being told that the state of the
            // connection has changed
            switch(event.getState()) 
            {
	            case SyncConnected:
	                // In this particular example we don't need to do anything
	                // here - watches are automatically re-registered with 
	                // server and any watches triggered while the client was 
	                // disconnected will be delivered (in order of course)
	                break;
	            case Expired:
	                // It's all over
	                this.running = ServerState.SERVER_STOPPED;
	                break;
            }
        }
        else 
        {
            if (path != null && path.equals(znode)) 
            {
            	try
            	{
	            	Stat stat = zk.exists(znode, this);
	            	byte metadata[] = zk.getData(znode, this, stat);
	            	child = zk.getChildren(znode, this, stat);
	            	ECSNode node = new ECSNode(metadata);
	            	
	            	if(event.getType() == Watcher.Event.EventType.NodeChildrenChanged)
	            	{
	            		running = ServerState.SERVER_WRITE_LOCK;
	            		if(child.size() > 0)
	            		{
	            			// get the metadata of the new server (available from the child of the zookeeper node)and start to transfer data to it
	            			
	            			stat = zk.exists(child.get(0), this);
	    	            	byte newmetadata[] = zk.getData(child.get(0), this, stat);
	    	            	
	            			ECSNode newserver = new ECSNode(newmetadata);
	            			
	            			// using direct tcp socket
	            			ServerTransfer move = new ServerTransfer(newserver, zk, child.get(0), nosql);
	            			new Thread(move).start();
	            		}
	            			
	            	}
	            	meta.setNode(node);
            	}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
                // Something has changed on the node, let's find out
            	
            	try
            	{
	            	Stat stat = zk.exists(znode, this);
	            	byte metadata[] = zk.getData(znode, this, stat);
	            	ECSNode node = new ECSNode(metadata);
	            	
	            	meta.setNode(node);
            	}
            	catch(Exception ex)
            	{
            		ex.printStackTrace();
            	}
            }
        }
	}
	
	@Override
	public void run()
	{
		if(server != null) 
		{
			while(isRunning())
			{
			    try 
			    {
					Socket client = server.accept();                
					ClientConnection connection = new ClientConnection(client, cache, meta);
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
		if(this.running == ServerState.SERVER_RUNNING)
			return true;
		else
			return false;
	}
	@Override
	public int getPort(){
		// TODO Auto-generated method stub
		return serverport;
	}

	@Override
    public String getHostname(){
		// TODO Auto-generated method stub
		return server.getInetAddress().getHostName();
	}

	@Override
    public CacheStrategy getCacheStrategy(){
		// TODO Auto-generated method stub
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
		return false;
	}

	@Override
    public boolean inCache(String key){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public String getKV(String key) throws Exception{
		// TODO Auto-generated method stub
		return "";
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		// TODO Auto-generated method stub
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

	@Override
	public void start() {
		// TODO
		running = ServerState.SERVER_RUNNING;
	}

    @Override
    public void stop() 
    {
		// TODO
    	
	}

    @Override
    public void lockWrite() {
		// TODO
	}

    @Override
    public void unlockWrite() {
		// TODO
	}

    @Override
    public boolean moveData(String[] hashRange, String targetName) throws Exception {
		// TODO
		return false;
	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
    	try {
    		new LogSetup("logs/server.log", Level.ALL);
			if(args.length != 3) {
				System.out.println("Error! Invalid number of arguments!");
				System.out.println("Usage: Server <port> <cache_size> <strategy>!");
			} else {
				int port = Integer.parseInt(args[0]);
				int size = Integer.parseInt(args[1]);
				String strategy = args[2];
				new Thread(new KVServer(port, size, strategy)).run();
			}
		} catch (IOException e) {
			System.out.println("Error! Unable to initialize logger!");
			e.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port> or <cache_size>! Not a number!");
			System.out.println("Usage: Server <port> <cache_size> <strategy>!");
			System.exit(1);
		}
 }
}
