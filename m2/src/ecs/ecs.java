package ecs;
import java.lang.*;
import org.apache.zookeeper.*;
import ecs.IECSNode;

public class ecs 
{
	ZooKeeper zk;
	final int zkport = 2181;
	String zkhost = "127.0.0.1";
	
	public ecs()
	{
		zk = new ZooKeeper();
		
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
