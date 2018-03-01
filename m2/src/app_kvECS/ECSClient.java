package app_kvECS;

import java.io.IOException;
import java.util.Map;
import java.util.Collection;
import java.security.MessageDigest;
import org.apache.zookeeper.*;
import ecs.*;
import ecs.IECSNode;

public class ECSClient implements IECSClient, Watcher 
{
	static ZooKeeper zk;
	final int zkport = 2181;
	int servercount = 0;
	String zkhost = "127.0.0.1";
	
    @Override
    public boolean start() {
        // TODO
        return false;
    }

    @Override
    public boolean stop() {
        // TODO
        return false;
    }

    @Override
    public boolean shutdown() {
        // TODO
        return false;
    }

    public void create(String path, byte[] data) throws KeeperException,InterruptedException 
	{
		this.zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
    
    @Override
    public IECSNode addNode(String cacheStrategy, int cacheSize) {
        // TODO
    	Process proc;
    	String script = "script.sh";

    	Runtime run = Runtime.getRuntime();
    	try {
    	  proc = run.exec(script);
    	} catch (IOException e) {
    	  e.printStackTrace();
    	}

    	ECSNode node = new ECSNode();
    	return node;
        //return null;
    }

    @Override
    public Collection<IECSNode> addNodes(int count, String cacheStrategy, int cacheSize) {
        // TODO
        return null;
    }

    @Override
    public Collection<IECSNode> setupNodes(int count, String cacheStrategy, int cacheSize) {
        // TODO
        return null;
    }

    @Override
    public boolean awaitNodes(int count, int timeout) throws Exception {
        // TODO
        return false;
    }

    @Override
    public boolean removeNodes(Collection<String> nodeNames) {
        // TODO
        return false;
    }

    @Override
    public Map<String, IECSNode> getNodes() {
        // TODO
        return null;
    }

    @Override
    public IECSNode getNodeByKey(String Key) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        // TODO
    	try 
		{
			zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
