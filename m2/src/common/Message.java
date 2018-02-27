package common;
import java.io.*;

import ecs.IECSNode;

public class Message implements KVMessage, Serializable
{
	public String key;
	public String value;
	public StatusType status;
	
	/**
	 * Creates an instance of the Message class for network client-server interaction.
	 *
	 * @param key
	 *            the key associated with this message (if applicable)
	 * @param value
	 *            the value associated with this message (if applicable) that is indexed by the given key.
	 * @param status
	 *            the type of this message that defines the type of operation if sent from the client or the response if sent from the server
	 */
	public Message()
	{
		key = null;
		value = null;
	}
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
	public IECSNode getResponsibleServer()
	{
		
	}
}