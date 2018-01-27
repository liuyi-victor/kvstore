package app_kvClient;

import client.*;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import logger.LogSetup;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//can leave this part of the client for later until testing

public class KVClient implements IKVClient 
{
	KVStore client;
	
	public enum SocketStatus{CONNECTED, DISCONNECTED, CONNECTION_LOST}; // TODO ?
	
	private static Logger logger = Logger.getRootLogger();
	private static final String PROMPT = "KVClient> ";
	private BufferedReader stdin;
	private boolean stop = false;
	
	private String serverAddress;
	private int serverPort;
	
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
		if(tokens[0].equals("quit")) {	
			stop = true;
			disconnect();
			System.out.println(PROMPT + "Application exit!");
		
		} else if (tokens[0].equals("connect")){
			if(tokens.length == 3) {
				try{
					serverAddress = tokens[1];
					serverPort = Integer.parseInt(tokens[2]);
					connect(serverAddress, serverPort);
				} catch(Exception e) {
					logger.warn("Attempt to connect to server failed");
					if(e instanceof UnknownHostException) {
						printError("Unknown Host!");
						logger.info("Unknown Host!", e);
					} else if(e instanceof NumberFormatException) {
						printError("No valid address. Port must be a number!");
						logger.info("Unable to parse argument <port>", e);
					} else if(e instanceof IOException) { 
						printError("Could not establish connection!");
						logger.warn("Could not establish connection!", e);
					}
				}
			} else {
				printError("Invalid number of parameters!");
			}
			
		} else if(tokens[0].equals("disconnect")) {
			disconnect();
			
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
			}
			
		} else if(tokens[0].equals("help")) {
			printHelp();
		} else if(tokens[0].equals("put")) {
			// TODO
			
			if(tokens.length > 1) {
				if(client != null && client.isRunning()){
					String key = tokens[1];
					String value;
					if(tokens.length>2) {
						StringBuilder msg = new StringBuilder();
						for(int i = 2; i < tokens.length; i++) {
							msg.append(tokens[i]);
							if (i != tokens.length -1 ) {
								msg.append(" ");
							}
						}
						value = msg.toString();
						System.out.println(value);
					} else {
						value = "";
					}
					put(key,value);
				} else {
					printError("Not connected!");
				}
			} else {
				printError("Message is empty!");
				printHelp();
			}
		} else if(tokens[0].equals("get")) {
			if(tokens.length == 2) {
				get(tokens[1]);
			}
		} else {
			printError("Unknown command");
			printHelp();
		}
	}
	
	// TODO add handle for get/put
	private void put(String key, String value){
		KVMessage msg;
		try {
			msg = client.put(key,value);
			switch(msg.getStatus()) {
			case PUT_SUCCESS:
				System.out.println("Record {"+msg.getKey()+" , "+msg.getValue()+"} successfully put");
				break;
			case PUT_UPDATE:
				System.out.println("Record {"+msg.getKey()+" , "+msg.getValue()+"} successfully updated");
				break;
			case PUT_ERROR:
				System.out.println("Error in putting record!");
				break;
			default:
				System.out.println("Invalid return received");
				break;
			}
		} catch (Exception e) {
			printError("Unable to send message!");
			disconnect();
		}
	}
	
	private void get(String key){
		KVMessage msg;
		try {
			msg = client.get(key);
			switch(msg.getStatus()) {
			case GET_SUCCESS:
				System.out.println("Record {"+msg.getKey()+" , "+msg.getValue()+"}");
				break;
			case GET_ERROR:
				System.out.println("Error in getting record!");
				break;
			default:
				System.out.println("Invalid return received");
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error in getting record!");
		}
		
//		try {
////			client.get(msg);
//		} catch (IOException e) {
//		}
	}

	
	private void printHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(PROMPT).append("ECHO CLIENT HELP (Usage):\n");
		sb.append(PROMPT);
		sb.append("::::::::::::::::::::::::::::::::");
		sb.append("::::::::::::::::::::::::::::::::\n");
		sb.append(PROMPT).append("connect <host> <port>");
		sb.append("\t establishes a connection to a server\n");
		sb.append(PROMPT).append("send <text message>");
		sb.append("\t\t sends a text message to the server \n");
		sb.append(PROMPT).append("disconnect");
		sb.append("\t\t\t disconnects from the server \n");
		
		sb.append(PROMPT).append("put <key> [value]");
		sb.append("\t\t put an entry to server or delete key if value=\"\" \n");
		
		sb.append(PROMPT).append("logLevel");
		sb.append("\t\t\t changes the logLevel \n");
		sb.append(PROMPT).append("\t\t\t\t ");
		sb.append("ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF \n");
		
		sb.append(PROMPT).append("quit ");
		sb.append("\t\t\t exits the program");
		System.out.println(sb.toString());
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
	
	public void handleNewMessage(String msg) {
		if(!stop) {
			System.out.println(msg);
			System.out.print(PROMPT);
		}
	}
	
//	public void handleStatus(SocketStatus status) {
//		if(status == SocketStatus.CONNECTED) {
//
//		} else if (status == SocketStatus.DISCONNECTED) {
//			System.out.print(PROMPT);
//			System.out.println("Connection terminated: " 
//					+ serverAddress + " / " + serverPort);
//			
//		} else if (status == SocketStatus.CONNECTION_LOST) {
//			System.out.println("Connection lost: " 
//					+ serverAddress + " / " + serverPort);
//			System.out.print(PROMPT);
//		}
//		
//	}

	private void printError(String error){
		System.out.println(PROMPT + "Error! " +  error);
	}
	

	private void connect(String address, int port) throws Exception {
		//TODO duplicate with newConnection()?
		// TODO should the exception check be changed, then the interface declaration needs changing
		newConnection(address,port);
	
	}
	
	private void disconnect() {
		if(client != null) {
			client.disconnect();
			client = null;
		}
	}
	
    @Override
    public void newConnection(String hostname, int port) throws Exception{
        // TODO Auto-generated method stub
		
		client = new KVStore(hostname, port);
		client.connect();
			
	//	if(e.equals(obj)UnknownHostException unknowhost)
	//	{
	//		logger.error(unknowhost.getMessage());
	//	}
	//	catch(IOException ioerror)
	//	{
	//		logger.error(ioerror.getMessage());
	//		
	//	}
	//	catch(IllegalArgumentException porterror)
	//	{
	//		logger.error(porterror.getMessage());
	//	}	
    }

    @Override
    public KVCommInterface getStore(){
        return client;
    }
    
    /**
     * Main entry point for the KVClient application. 
     * @param args contains the port number at args[0].
     */
    public static void main(String[] args) {
    		try {
			new LogSetup("logs/client.log", Level.OFF);
			KVClient kvc = new KVClient();
			kvc.run();
		} catch (IOException e) {
			System.out.println("Error! Unable to initialize logger!");
			e.printStackTrace();
			System.exit(1);
		}
		
    }
}


