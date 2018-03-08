package common;

import java.io.*;

public class KVTransfer implements Serializable
{
	public String key;
	public String value;
	
	public KVTransfer()
	{
		key = null;
		value = null;
	}
	public KVTransfer(String key, String value)
	{
		this.key = key;
		this.value = value;
	}
	public String getKey()
	{
		return key;
	}
	
	public String getValue()
	{
		return value;
	}
}
