package client;

import common.messages.KVMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class KVStore implements KVCommInterface {

	String addr;
	int client_port;
	Socket client;

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
	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		client.close();
	}

	@Override
	public KVMessage put(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		// sends a message to the server with the key and value pair for insertion or update
		return null;
	}

	@Override
	public KVMessage get(String key) throws Exception {
		// TODO Auto-generated method stub
		// sends a message to the server that retrieves the value of the given key
		return null;
	}
}
