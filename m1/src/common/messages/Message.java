package common.messages;

import java.io.*;
import java.io.Serializable;

public class Message implements KVMessage, Serializable
{
	String key;
	String value;
	StatusType status;
	
	public Message(String key, String value, StatusType status)
	{
		this.key = key;
		this.value = value;
		this.status = status;
	}
	public String getKey()
	{
		return key;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public StatusType getStatus()
	{
		return status;
	}
	/*
	private void writeObject(java.io.ObjectOutputStream stream) throws IOException
	{
		
	}
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		
	}
	*/
}