
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
	
	private boolean running;
	
//	private ObjectInputStream readobj;
//	private ObjectOutputStream writeobj;
	
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
			client = new Socket(addr, client_port);
			fromserver = client.getInputStream();
			toserver = client.getOutputStream();
			writeobj = new ObjectOutputStream(toserver);
			readobj = new ObjectInputStream(fromserver);
			running = true;
	}

	@Override
	public void disconnect() {
		try
		{
			writeobj.close();
			readobj.close();
			fromserver.close();
			toserver.close();
			client.close();
			
			running = false;
		}
		catch(Exception ex)
		{
			
		}
	}
	
	public boolean isRunning() {
		return running;
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
//		ObjectOutputStream writeobj = new ObjectOutputStream(toserver);
//		ObjectInputStream readobj = new ObjectInputStream(fromserver);
		
		Message request = new Message(key, value, KVMessage.StatusType.PUT);
		System.out.println("Key:"+key+" value: "+value+" put");
		System.out.println("Key:"+request.key+" value: "+request.value+" "+request.getStatus().toString());
		// TODO remove
		writeobj.writeObject(request);
		Message reply = (Message)readobj.readObject();
		// TODO check to see if this will cause a wait? should
//		writeobj.close();
//		readobj.close();
		
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
//		ObjectOutputStream writeobj = new ObjectOutputStream(toserver);
//		ObjectInputStream readobj = new ObjectInputStream(fromserver);
		
		Message request = new Message(key, null, KVMessage.StatusType.GET);
		writeobj.writeObject(request);
		Message reply = (Message)readobj.readObject();
//		writeobj.close();
//		readobj.close();
		return reply;
	}
}
