package ecs;

import java.io.Serializable;

import org.apache.zookeeper.*;

public class ECSNode implements IECSNode, Serializable
{
	public String name;
	public String address;
	public int port;
	public String lowerHash;
	public String upperHash;
	
	public ECSNode() 
	{
		
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
		String meta = "name: "+this.name + "\n" + "address: "+this.address + "\n" + "port: "+ Integer.toString(this.port) + "\n" + "lowerHash: "+this.lowerHash + "\n" + "upperHash: "+this.upperHash;
		return meta.getBytes();
	}
}
