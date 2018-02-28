package ecs;
import java.lang.*;
import org.apache.zookeeper.*;
import java.security.MessageDigest;
import ecs.IECSNode;

public class ecs implements Watcher
{
	static ZooKeeper zk;
	final int zkport = 2181;
	int servercount = 0;
	String zkhost = "127.0.0.1";
	//Watcher watch;
	public ecs()
	{
		//watch = new Watcher();
		zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
		
	}
	public void create(String path, byte[] data) throws KeeperException,InterruptedException 
	{
		this.zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	public boolean start()
	{
		
	}
	public static void main(String[] args) 
	{
		//read the command line arguments
		
		//parse the config file
	}
}
