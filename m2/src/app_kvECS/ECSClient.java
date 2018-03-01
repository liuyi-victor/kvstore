package app_kvECS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.*;
import logger.LogSetup;
import org.apache.zookeeper.*;
import ecs.*;
import ecs.IECSNode;

public class ECSClient implements IECSClient, Watcher 
{
	static ZooKeeper zk;
	final int zkport = 2181;
	int servercount = 0;
	String zkhost = "127.0.0.1";
	Logger logger = Logger.getRootLogger();
	
	ECSClient(List<String> names,List<String> addresses,List<String> ports){
		try {
			zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
    		try {
				new LogSetup("logs/server.log", Level.ALL);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        // TODO
    		if(args.length != 1) {
    			System.err.println("USAGE: java -jar m2-ecs.jar ecs.config");
    			System.exit(2);
    		}
    		
    		List<String> names = new Vector<String>();
		List<String> addresses = new Vector<String>();
		List<String> ports = new Vector<String>();
    		
    		// Parse ECS config file and put value inside list
		try {
			String filename = args[0];
			File file = new File(filename);
			if(file.isFile())
			{
				List<String> allLines = Files.readAllLines(file.toPath());
				
				for(String line : allLines) {
//					System.out.println("Original line: "+line);
					StringTokenizer st = new StringTokenizer(line);
					// Skip invalid entries
					if(st.countTokens() != 3) {
//						System.out.println("Num tokens "+st.countTokens());
						continue;
					}
					names.add(st.nextToken());
					// TODO if not needed, can combine address port here
					addresses.add(st.nextToken());
					ports.add(st.nextToken());
				}
				// TODO possibly add detection for duplicate name / address+port
				
			}else {
				System.err.println("File does not exist!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(names);
		System.out.println(addresses);
		System.out.println(ports);
		
    		ECSClient ec = new ECSClient(names,addresses,ports);
    		ec.start();
    	/*try 
		{
			zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
			
			// TODO Parser goes here
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
}
