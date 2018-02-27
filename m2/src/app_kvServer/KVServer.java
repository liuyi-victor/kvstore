package app_kvServer;

import java.net.ServerSocket;

import org.apache.log4j.Logger;

import app_kvServer.IKVServer.CacheStrategy;
import org.apache.zookeeper.*;

public class KVServer implements IKVServer {

	int serverport;
	int cache_size;
	CacheStrategy replacement;
	ServerSocket server;
	private final int wellknowports = 1024;
	private static Logger logger = Logger.getRootLogger();
	private boolean running;
	private static Database nosql = new Database();
	private static Cache cache = new Cache(); 
	
	
	private ZooKeeper zk;
	private Watcher watch;
	/**
	 * Start KV Server with selected name
	 * @param name			unique name of server
	 * @param zkHostname	hostname where zookeeper is running
	 * @param zkPort		port where zookeeper is running
	 */
	public KVServer(String name, String zkHostname, int zkPort) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getPort(){
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
    public String getHostname(){
		// TODO Auto-generated method stub
		return null;
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
