package ecs;

import org.apache.zookeeper.*;

public class ECSNode implements IECSNode
{
	String name;
	String address;
	int port;
	
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
		
	}
}
