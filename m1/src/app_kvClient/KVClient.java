package app_kvClient;

import client.KVCommInterface;

import java.net.Socket;
import java.io.IOException;

//can leave this part of the client for later until testing

public class KVClient implements IKVClient {
	KVStore client;
    @Override
    public void newConnection(String hostname, int port) throws Exception{
        // TODO Auto-generated method stub
	try
	{
		//client = new KVStore()
	}
	catch(Exception e)
	{

	
	}
}

    @Override
    public KVCommInterface getStore(){
        // TODO Auto-generated method stub
        return client;
    }
}
