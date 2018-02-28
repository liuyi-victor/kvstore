package app_kvServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import app_kvServer.IKVServer.CacheStrategy;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;

public class KVServer implements IKVServer, Runnable, Watcher, StatCallback {

	int serverport;
	int cache_size;
	CacheStrategy replacement;
	ServerSocket server;
	private final int wellknowports = 1024;
	private static Logger logger = Logger.getRootLogger();
	private ServerState running;
	private static Database nosql = new Database();
	private static Cache cache = new Cache(); 
	
	
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
	public KVServer(String name, String zkHostname, int zkPort) {
		// TODO Auto-generated method stub
		zkhost = zkHostname;
		zkport = zkPort;
		this.znode = name;
		
		//initiate a zookeeper client at this KVServer
		zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
		zk.exists(name, true, this, null);
	}

	public void process(WatchedEvent event)
	{
		String path = event.getPath();
        if (event.getType() == Event.EventType.None) 
        {
            // We are are being told that the state of the
            // connection has changed
            switch (event.getState()) 
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
                // Something has changed on the node, let's find out
            	Stat stat = zk.exists(znode, true);
            	byte metadata[] = zk.getData(znode, this, stat);
            }
        }
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
					ClientConnection connection = new ClientConnection(client, cache);
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
    public void run(){
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
	}

    @Override
    public void stop() {
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
}
