package app_kvServer;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.io.IOException;

import logging.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import common.messages.*;

public class KVServer implements IKVServer, Runnable {

	int serverport;
	int cache_size;
	String replacement;
	ServerSocket server;


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
		// TODO Auto-generated method stub
		// Will possibly need to do a check to ensure correct params are passed in
		this.replacement = strategy;
		this.cache_size = cacheSize;	
		this.serverport = port;
		initializeServer();
		//listener = new clientlistener(server);
		//listener.start();
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

	private boolean initializeServer() 
	{
		logger.info("Initialize server ...");
		try 
		{
		    this.server = new ServerSocket(this.serverport);
		    logger.info("Server listening on port: " 
		    		+ serverSocket.getLocalPort());    
		    return true;

		} 
		catch (IOException e) 
		{
			logger.error("Error! Cannot open server socket:");
			if(e instanceof BindException){
				logger.error("Port " + port + " is already bound!");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public CacheStrategy getCacheStrategy(){
		return IKVServer.CacheStrategy.valueOf(replacement.toUpperCase(Locale.ENGLISH));
	}

	@Override
    public int getCacheSize(){
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

	// Helper function for checking if cache is full
	public boolean isCacheFull() {
		// TODO compare variable cache_size with current size
		return false;
	}
	
	@Override
    public String getKV(String key) throws Exception{
		// TODO Auto-generated method stub
		if(isCacheFull()) {
			
			// Based on cache strategy apply different method
			switch(this.replacement) {
				case "NONE":
					break;
				case "LRU":
					break;
				case "LFU":
					break;
				case "FIFO":
					break;	
			}
		}
		return "";
	}

	@Override
    public void putKV(String key, String value) throws Exception{
		// TODO Auto-generated method stub
		if(isCacheFull()) {
			// Based on cache strategy apply different method
			switch(this.replacement) {
				case "NONE":
					break;
				case "LRU":
					break;
				case "LFU":
					break;
				case "FIFO":
					break;	
			}
		}
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
}


/*
public class clientlistener extends Thread
{
	ServerSocket serverSocket;
	int port;
	public clientlistener(int serverport)
	{
		server = host;
		port = serverport;
		serverSocket = new ServerSocket(port);
	}
	public clientlistener(ServerSocket socket)
	{
		serverSocket = socket;
	}
	public void run()
	{
		if(serverSocket != null) 
		{
			while(isRunning())
			{
			    try {
				Socket client = serverSocket.accept();                
				ClientConnection connection = 
						new ClientConnection(client);
				new Thread(connection).start();
		
				logger.info("Connected to " 
						+ client.getInetAddress().getHostName() 
						+  " on port " + client.getPort());
			    } catch (IOException e) {
			    	logger.error("Error! " +
			    			"Unable to establish connection. \n", e);
			    }
	        	}
        	}
        	logger.info("Server stopped.");
	}
}
public class ClientConnection implements Runnable
{
	private static Logger logger = Logger.getRootLogger();
	
	private boolean isOpen;
	private static final int BUFFER_SIZE = 1024;
	private static final int DROP_SIZE = 128 * BUFFER_SIZE;
	
	private Socket clientSocket;
	private InputStream input;
	private OutputStream output;
	

	public ClientConnection(Socket clientSocket) {
		this.clientSocket = clientSocket;
		this.isOpen = true;
	}
	

	public void run() {
		try {
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
		
			sendMessage(new Message(
					"Connection to MSRG Echo server established: " 
					+ clientSocket.getLocalAddress() + " / "
					+ clientSocket.getLocalPort()));
			
			while(isOpen) {
				try {
					Message latestMsg = receiveMessage();
					sendMessage(latestMsg);
					

				} catch (IOException ioe) {
					logger.error("Error! Connection lost!");
					isOpen = false;
				}				
			}
			
		} catch (IOException ioe) {
			logger.error("Error! Connection could not be established!", ioe);
			
		} finally {
			
			try {
				if (clientSocket != null) {
					input.close();
					output.close();
					clientSocket.close();
				}
			} catch (IOException ioe) {
				logger.error("Error! Unable to tear down connection!", ioe);
			}
		}
	}
	

	public void sendMessage(Message msg) throws IOException {
		byte[] msgBytes = msg.getMsgBytes();
		output.write(msgBytes, 0, msgBytes.length);
		output.flush();
		logger.info("SEND \t<" 
				+ clientSocket.getInetAddress().getHostAddress() + ":" 
				+ clientSocket.getPort() + ">: '" 
				+ msg.getMsg() +"'");
    }
	
	
	private Message receiveMessage() throws IOException {
		
		int index = 0;
		byte[] msgBytes = null, tmp = null;
		byte[] bufferBytes = new byte[BUFFER_SIZE];
		

		byte read = (byte) input.read();	
		boolean reading = true;
		
//		logger.info("First Char: " + read);
//		Check if stream is closed (read returns -1)
//		if (read == -1){
//			TextMessage msg = new TextMessage("");
//			return msg;
//		}

		while(read != 10 && read !=-1 && reading) {
			if(index == BUFFER_SIZE) {
				if(msgBytes == null){
					tmp = new byte[BUFFER_SIZE];
					System.arraycopy(bufferBytes, 0, tmp, 0, BUFFER_SIZE);
				} else {
					tmp = new byte[msgBytes.length + BUFFER_SIZE];
					System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
					System.arraycopy(bufferBytes, 0, tmp, msgBytes.length,
							BUFFER_SIZE);
				}

				msgBytes = tmp;
				bufferBytes = new byte[BUFFER_SIZE];
				index = 0;
			} 
			
			bufferBytes[index] = read;
			index++;
			
			if(msgBytes != null && msgBytes.length + index >= DROP_SIZE) {
				reading = false;
			}
			
			read = (byte) input.read();
		}
		
		if(msgBytes == null){
			tmp = new byte[index];
			System.arraycopy(bufferBytes, 0, tmp, 0, index);
		} else {
			tmp = new byte[msgBytes.length + index];
			System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
			System.arraycopy(bufferBytes, 0, tmp, msgBytes.length, index);
		}
		
		msgBytes = tmp;
		
		Message msg = new Message(msgBytes);
		logger.info("RECEIVE \t<" 
				+ clientSocket.getInetAddress().getHostAddress() + ":" 
				+ clientSocket.getPort() + ">: '" 
				+ msg.getMsg().trim() + "'");
		return msg;
    }
}
*/