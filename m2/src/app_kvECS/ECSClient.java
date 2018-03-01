package app_kvECS;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
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
	
	private BufferedReader stdin;
	private boolean stop = false;
	
	private static final String PROMPT = "ECSClient> ";
	
	ECSClient(List<String> names,List<String> addresses,List<String> ports){
		try {
			zk = new ZooKeeper(zkhost+":"+zkport, 3000, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(!stop) {
			stdin = new BufferedReader(new InputStreamReader(System.in));
			System.out.print(PROMPT);
			
			try {
				String cmdLine = stdin.readLine();
				this.handleCommand(cmdLine);
			} catch (IOException e) {
				stop = true;
				printError("CLI does not respond - Application terminated ");
			}
		}
	}
	
	private void handleCommand(String cmdLine) {
		String[] tokens = cmdLine.split("\\s+");
		
// TODO Switch to switch statement 
		if(tokens[0].equals("shutdown")) {	
//			stop = true;
			shutdown();
			System.out.println(PROMPT + "Application exit!");
		
		} else if (tokens[0].equals("start")){
			if(tokens.length == 1) {
				start();
			} else {
				printError("Invalid number of parameters!");
			}
		} else if(tokens[0].equals("stop")) {
			stop();
			
		} else if(tokens[0].equals("logLevel")) {
			if(tokens.length == 2) {
				String level = setLevel(tokens[1]);
				if(level.equals(LogSetup.UNKNOWN_LEVEL)) {
					printError("No valid log level!");
					printPossibleLogLevels();
				} else {
					System.out.println(PROMPT + 
							"Log level changed to level " + level);
				}
			} else {
				printError("Invalid number of parameters!");
				printHelp();
			}
			
		} else if(tokens[0].equals("addnodes")) {
			// TODO
			
			if(tokens.length == 4) {
				addNodes(Integer.valueOf(tokens[1]), tokens[2], Integer.valueOf(tokens[3]));
			} else {
				printError("Invalid number of parameters!");
				printHelp();
			}
		} else if(tokens[0].equals("addnode")) {
			if(tokens.length == 3) {
				addNode(tokens[1], Integer.valueOf(tokens[2]));
			}
		} else if(tokens[0].equals("removenode")) {
			// TODO
			if(tokens.length == 2) {
				removeNode(Integer.valueOf(tokens[1]));
			} else {
				printError("Invalid number of parameters!");
				printHelp();
			}
		} else if(tokens[0].equals("help")) {
			printHelp();
		} else {
			printError("Unknown command");
			printHelp();
		}
	}
	
	

	private void printPossibleLogLevels() {
		System.out.println(PROMPT 
				+ "Possible log levels are:");
		System.out.println(PROMPT 
				+ "ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF");
	}
	
	private String setLevel(String levelString) {
		
		if(levelString.equals(Level.ALL.toString())) {
			logger.setLevel(Level.ALL);
			return Level.ALL.toString();
		} else if(levelString.equals(Level.DEBUG.toString())) {
			logger.setLevel(Level.DEBUG);
			return Level.DEBUG.toString();
		} else if(levelString.equals(Level.INFO.toString())) {
			logger.setLevel(Level.INFO);
			return Level.INFO.toString();
		} else if(levelString.equals(Level.WARN.toString())) {
			logger.setLevel(Level.WARN);
			return Level.WARN.toString();
		} else if(levelString.equals(Level.ERROR.toString())) {
			logger.setLevel(Level.ERROR);
			return Level.ERROR.toString();
		} else if(levelString.equals(Level.FATAL.toString())) {
			logger.setLevel(Level.FATAL);
			return Level.FATAL.toString();
		} else if(levelString.equals(Level.OFF.toString())) {
			logger.setLevel(Level.OFF);
			return Level.OFF.toString();
		} else {
			return LogSetup.UNKNOWN_LEVEL;
		}
	}
	
	private void printHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(PROMPT).append("ECHO CLIENT HELP (Usage):\n");
		sb.append(PROMPT);
		sb.append("::::::::::::::::::::::::::::::::");
		sb.append("::::::::::::::::::::::::::::::::\n");
		sb.append(PROMPT).append("start");
		sb.append("\t establishes a connection to a server\n");
		sb.append(PROMPT).append("stop");
		sb.append("\t\t sends a text message to the server \n");
		sb.append(PROMPT).append("shutdown");
		sb.append("\t\t\t disconnects from the server \n");
		
		sb.append(PROMPT).append("addnodes <#servers> <cacheStrategy> <cacheSize>");
		sb.append("\t\t put an entry to server or delete key if value=\"\" \n");
		
		sb.append(PROMPT).append("addnode <cacheStrategy> <cacheSize>");
		sb.append("\t\t put an entry to server or delete key if value=\"\" \n");
		
		sb.append(PROMPT).append("logLevel");
		sb.append("\t\t\t changes the logLevel \n");
		sb.append(PROMPT).append("\t\t\t\t ");
		sb.append("ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF \n");
		
		sb.append(PROMPT).append("quit ");
		sb.append("\t\t\t exits the program");
		System.out.println(sb.toString());
	}
	
	private void printError(String error){
		System.out.println(PROMPT + "Error! " +  error);
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

    private void removeNode(Integer serverIndex) {
		// TODO Auto-generated method stub
		
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
    		ec.run();
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
