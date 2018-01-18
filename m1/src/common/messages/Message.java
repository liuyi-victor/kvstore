package common.messages;

import java.io.*;
import java.io.Serializable;

public class Message implements KVMessage, Serializable
{
	String key;
	String value;
	StatusType status;
	
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
		return KVMessage.StatusType
	}
}