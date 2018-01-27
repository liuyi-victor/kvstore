package app_kvClient;

import client.*;

import java.net.Socket;
import java.io.IOException;

//can leave this part of the client for later until testing

public class KVClient implements IKVClient 
{
	//KVStore client;
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
        //return client;
    	return null;
    }
    public static void main(String[] args) 
    {
    	KVStore client;
    	client = new KVStore("localhost", 50000);
    	try
    	{
    		client.connect();
    		System.out.println(client.put("foo", "bar"));
    		System.out.println(client.get("foo").getValue());
    		//System.out.println(client.put("foo1", "bar2"));
    	}
    	catch(Exception ex)
    	{
    		
    	}
    }
}
