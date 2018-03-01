package app_kvServer;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.IOException;

import org.apache.log4j.Logger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import common.*;
import common.KVMessage.StatusType;
import java.security.MessageDigest;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.apache.zookeeper.*;
import ecs.*;

public class ClientConnection implements Runnable
{
	private static Logger logger = Logger.getRootLogger();
	
	private boolean isOpen;
	//private static final int BUFFER_SIZE = 1024;
	//private static final int DROP_SIZE = 128 * BUFFER_SIZE;
	//private static final int linecount = 3;
	
	private Socket clientSocket;
	private InputStream input;
	private OutputStream output;
	private Cache cache;
//	private static Database nosql = new Database();
	private ObjectInputStream readobj;
	private ObjectOutputStream writeobj;
	private ECSNode meta;
	//private static Vector<String> queue;		//used as a request queue that buffers requests to the cache
	
	public ClientConnection(Socket clientSocket, Cache cache, ECSNode node) {
		this.clientSocket = clientSocket;
		this.isOpen = true;
		this.cache = cache; 
		this.meta = node;
	}
	
	private boolean hashRange(String hash)
	{
		if(meta.lowerHash.compareTo(meta.upperHash) < 0)
		{
			//does not wrap around the ring
			if(meta.lowerHash.compareTo(hash) < 0 && meta.upperHash.compareTo(hash) > 0 )
				return true;
			else 
				return false;
		}
		else
		{
			if(meta.lowerHash.compareTo(hash) > 0 && meta.upperHash.compareTo(hash) < 0 )
				return false;
			else 
				return true;
		}
	}
	// TODO add comments
	// TODO adjust the int success variable
	private boolean handleclient(Message msg, Message toclient)
	{
		//TODO: ADD THE LOGGING IN THIS FUNCTION
		
		KVMessage.StatusType type = msg.getStatus();
		String key = msg.getKey(); 
		String value = msg.getValue();
		toclient.key = key;
		toclient.value = value;
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		String hash = (new HexBinaryAdapter()).marshal((md.digest(key.getBytes())));
		if(!hashRange(hash))
		{
			toclient.status = StatusType.SERVER_NOT_RESPONSIBLE;
		}
		
		int success;
		
		if(key.contains(" ")){
			switch(type){
			case GET:
				toclient.status = StatusType.GET_ERROR;
				success = -1;
				break;
			case PUT:
				if(value == null || value.isEmpty()){
					toclient.status = StatusType.DELETE_ERROR;
					success = -2;
				}else{
					toclient.status = StatusType.PUT_ERROR;
					success = -2;
				}
				break;
			default:
				success = -1;
				break;
			}
		}
		else if(type == StatusType.GET)
		{
			// TODO Problem with logic
				try {
					value = (String)cache.access(key, null, false);
//					value = cache.get(key);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(key.length() > 20)
				{
					success = -1;
					toclient.status = StatusType.GET_ERROR;
				}
				else if(value == null)
				{
					success = -1;
					toclient.status = StatusType.GET_ERROR;
				}
				else
				{
					success = 1;
					toclient.status = StatusType.GET_SUCCESS;
				}
				toclient.value = value;
		}
		else //(type == StatusType.PUT)
		{
			// insert/update/delete operation
			// TODO change below function(s) currently using 0 as placeholder
//			success = storage.putKV(key, value);
			success = (int)cache.access(key, value, true);
//			success = cache.put(key,value);
//			logger.info(message)
			// DELETE Operation
			if(value == null || value.isEmpty()){
				
				if(key.length() > 20)
				{
					toclient.status = StatusType.DELETE_ERROR;
				}
				else if(success > 0)
				{
					toclient.status = StatusType.DELETE_SUCCESS;
				}
				else if(success < 0)
				{
					toclient.status = StatusType.DELETE_ERROR;
				}
			}
			// Insert/Modify Operation
			else {
				if(key.length() > 20)
				{
					toclient.status = StatusType.PUT_ERROR;
				}
				else if(success < 0)
				{
					toclient.status = StatusType.PUT_ERROR;
				}
				else
				{
					if(success == 1)
					{
						toclient.status = StatusType.PUT_SUCCESS;
						System.out.println("Test put success, key is: "+key+"\n length is: "+key.length());
					}
					else 
					{
						toclient.status = StatusType.PUT_UPDATE;
					}
				}
			}
		}
		if(success > 0)
			return true;
		else
			return false;
	}
	
	public void run() 
	{
		try {
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			readobj = new ObjectInputStream(input);
			writeobj = new ObjectOutputStream(output);
			
			while(isOpen) {
				try {
					try
					{
						Message latestMsg = (Message)readobj.readObject();//receiveMessage();
						logger.info("RECEIVE \t<" 
								+ clientSocket.getInetAddress().getHostAddress() + ":" 
								+ clientSocket.getPort() + ">: '" 
								+ latestMsg.getStatus()+ " "+latestMsg.key+ "' "+latestMsg.value);
						// TODO
						Message toclient = new Message();
						handleclient(latestMsg, toclient);
						writeobj.writeObject(toclient);//sendMessage(latestMsg);
					}
					catch(ClassNotFoundException notfound)
					{
						logger.error(notfound.getMessage());
						return;
					}
					/*
					Message toclient = new Message();
					handleclient(latestMsg, toclient);
					writeobj.writeObject(toclient);//sendMessage(latestMsg);*/
				} catch (IOException ioe) {
					logger.error("Error! Connection lost in run!");
					logger.error(ioe.getMessage());
					isOpen = false;
				}				
			}
			
		} 
		catch (IOException ioe) 
		{
			logger.error("Error! Connection could not be established!", ioe);
		} 
		finally 
		{
			
			try {
				if (clientSocket != null) 
				{
					readobj.close();
					writeobj.close();
					input.close();
					output.close();
					clientSocket.close();
				}
			} catch (IOException ioe) {
				logger.error("Error! Unable to tear down connection!", ioe);
			}
		}
	}
	
/* KEEP THESE TWO METHODS FOR NOW
	public void sendMessage(Message msg) throws IOException 
	{
		byte[] msgBytes = msg.getMsgBytes();
		output.write(msgBytes, 0, msgBytes.length);
		output.flush();
		logger.info("SEND \t<" 
				+ clientSocket.getInetAddress().getHostAddress() + ":" 
				+ clientSocket.getPort() + ">: '" 
				+ msg.getMsg() +"'");
    }
	
	
	private Message receiveMessage() throws IOException 
	{
		
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
//		
//		//parser for the message		
//		while(count < linecount && read !=-1 && reading) {
//			if(index == BUFFER_SIZE) {
//				if(msgBytes == null){
//					tmp = new byte[BUFFER_SIZE];
//					System.arraycopy(bufferBytes, 0, tmp, 0, BUFFER_SIZE);
//				} else {
//					tmp = new byte[msgBytes.length + BUFFER_SIZE];
//					System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
//					System.arraycopy(bufferBytes, 0, tmp, msgBytes.length,
//							BUFFER_SIZE);
//				}
//
//				msgBytes = tmp;
//				bufferBytes = new byte[BUFFER_SIZE];
//				index = 0;
//			} 
//			if(read == 10)
//				index = index + 1;
//			bufferBytes[index] = read;
//			index++;
//			
//			if(msgBytes != null && msgBytes.length + index >= DROP_SIZE) {
//				reading = false;
//			}
//			
//			read = (byte) input.read();
//		}
//		
//		if(msgBytes == null){
//			tmp = new byte[index];
//			System.arraycopy(bufferBytes, 0, tmp, 0, index);
//		} else {
//			tmp = new byte[msgBytes.length + index];
//			System.arraycopy(msgBytes, 0, tmp, 0, msgBytes.length);
//			System.arraycopy(bufferBytes, 0, tmp, msgBytes.length, index);
//		}
//		
//		msgBytes = tmp;
		

		String msgcontent = new String(msgBytes);
		String[] array = msgcontent.split("\n");
		Message msg = new Message(array[0], array[1], KVMessage.StatusType.valueOf(array[2]));
		logger.info("RECEIVE \t<" 
				+ clientSocket.getInetAddress().getHostAddress() + ":" 
				+ clientSocket.getPort() + ">: '" 
				+ msg.getMsg().trim() + "'");
		return msg;
    }
    */
}