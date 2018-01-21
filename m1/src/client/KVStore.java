package client;

import common.messages.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.Exception.*;
import org.apache.log4j.Logger;

public class KVStore implements KVCommInterface {

	String addr;
	int client_port;
	Socket client;
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
		Message msg = new Message(key, value, KVMessage.StatusType.PUT);
		return null;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		// sends a message to the server that retrieves the value of the given key
		Message msg = new Message(key, null, KVMessage.StatusType.GET);
		return null;
	}
}
