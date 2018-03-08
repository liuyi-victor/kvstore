package ecs;

import java.io.Serializable;
import app_kvServer.*;
import app_kvServer.KVServer.*;
import org.apache.zookeeper.*;

public class ECSNode implements IECSNode, Serializable
{
	public String name;
	public String address;
	public int port;
	public String lowerHash;
	public String upperHash;
	public ServerState state;
	
	public ECSNode() 
	{
		
	}
	public ECSNode(byte[] array)
	{
		String meta = array.toString();
		
		String metadata[] = meta.split("\n");
		this.name = metadata[0];
		this.address = metadata[1];
		this.port = Integer.parseInt(metadata[2]);
		this.lowerHash = metadata[3];
		this.upperHash = metadata[4];
		if(metadata[5] == ServerState.SERVER_RUNNING.name())
		{
			this.state = ServerState.SERVER_RUNNING;
		}
		else if(metadata[5] == ServerState.SERVER_STOPPED.name())
		{
			this.state = ServerState.SERVER_STOPPED;
		}
		else if(metadata[5] == ServerState.SERVER_WRITE_LOCK.name())
		{
			this.state = ServerState.SERVER_WRITE_LOCK;
		}
	}
	public ECSNode(String name, String address, int port, String lowerHash, String upperHash)
	{
		this.name = name;
		this.address = address;
		this.port = port;
		this.lowerHash = lowerHash;
		this.upperHash = upperHash;
	}
	public String getNodeName()
	{
		return name;
	}
	public String getNodeHost()
	{
		return address;
	}
	public int getNodePort()
	{
		return port;
	}
	public String[] getNodeHashRange()
	{
		String range[] = new String[2];
		range[0] = this.lowerHash;
		range[1] = this.upperHash;
		return range;
	}
	public byte[] toArray()
	{
		//String meta = "name: "+this.name + "\n" + "address: "+this.address + "\n" + "port: "+ Integer.toString(this.port) + "\n" + "lowerHash: "+this.lowerHash + "\n" + "upperHash: "+this.upperHash;
		String meta = this.name + "\n" + this.address + "\n" + Integer.toString(this.port) + "\n" + this.lowerHash + "\n" + this.upperHash + "\n" + this.state.name();
		return meta.getBytes();
	}
	public void setNode(ECSNode node)
	{
		this.name = node.name;
		this.address = node.address;
		this.port = node.port;
		this.lowerHash = node.lowerHash;
		this.upperHash = node.upperHash;
		this.state = node.state;
	}
}
