package client;

import common.messages.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.*;
import java.lang.Exception.*;
import org.apache.log4j.Logger;

public class KVStore implements KVCommInterface {

	private String addr;
	private int client_port;
	private Socket client;
	private InputStream fromserver;
	private OutputStream toserver;
	private ObjectInputStream readobj;
	private ObjectOutputStream writeobj;
	
	private Logger logger = Logger.getRootLogger();
	
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		// TODO Auto-generated method stub
		addr = address;
		client_port = port;	
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub
		
		client = new Socket(addr, client_port);
		fromserver = client.getInputStream();
		toserver = client.getOutputStream();
		writeobj = new ObjectOutputStream(toserver);
		readobj = new ObjectInputStream(fromserver);
		/*
		catch(UnknownHostException unknowhost)
		{
			logger.error(unknowhost.getMessage());
		}
		catch(IOException ioerror)
		{
			logger.error(ioerror.getMessage());
		}
		catch(IllegalArgumentException porterror)
		{
			logger.error(porterror.getMessage());
		}
		*/
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		try
		{
			writeobj.close();
			readobj.close();
			fromserver.close();
			toserver.close();
			client.close();
		}
		catch(Exception ex)
		{
			
		}
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		// sends a message to the server with the key and value pair for insertion or update
		if(key.length() > 20)
		{
			IllegalArgumentException argexception = new IllegalArgumentException("The length of the key cannot be greater than 20 bytes");
			throw argexception;
		}
		//writeobj = new ObjectOutputStream(toserver);
		//readobj = new ObjectInputStream(fromserver);
		
		Message request = new Message(key, value, KVMessage.StatusType.PUT);
		writeobj.writeObject(request);
		Message reply = (Message)readobj.readObject();
		
		//writeobj.close();
		//readobj.close();
		
		return reply;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		// sends a message to the server that retrieves the value of the given key
		
		if(key.length() > 20)
		{
			IllegalArgumentException argexception = new IllegalArgumentException("The length of the key cannot be greater than 20 bytes");
			throw argexception;
		}
		//writeobj = new ObjectOutputStream(toserver);
		//readobj = new ObjectInputStream(fromserver);
		
		Message request = new Message(key, null, KVMessage.StatusType.GET);
		writeobj.writeObject(request);
		Message reply = (Message)readobj.readObject();
		//writeobj.close();
		//readobj.close();
		return reply;
	}
}
