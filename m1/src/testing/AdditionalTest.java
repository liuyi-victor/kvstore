package testing;

import org.junit.Test;

import app_kvServer.KVServer;
import client.KVStore;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import junit.framework.TestCase;

public class AdditionalTest extends TestCase {
	
	// TODO add your test cases, at least 3
	private KVStore kvClient;
	
	public void setUp() {
		kvClient = new KVStore("localhost", 50000);
		try {
			kvClient.connect();
		} catch (Exception e) {
		}
	}

	public void tearDown() {
		kvClient.disconnect();
	}
	
	@Test
	public void testStub() {
		assertTrue(true);
	}
	
	@Test
	public void testDeleteNull() {
		String key = "deleteNullValue";
		String value = "toDelete";
		
		KVMessage response = null;
		Exception ex = null;
	
		try {
			kvClient.put(key, value);
			response = kvClient.put(key, null);
			
		} catch (Exception e) {
			ex = e;
		}
	
		assertTrue(ex == null && response.getStatus() == StatusType.DELETE_SUCCESS);
	}
	
	@Test
	public void testDeleteFail() {
		String key = "deleteFailValue";
		String value = "";
		
		KVMessage response = null;
		Exception ex = null;

		try {
			response = kvClient.put(key, value);
			
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.getStatus() == StatusType.DELETE_ERROR);
	}
	
	public void testAddMultiple() {
		String key;
		String value;
		
		KVMessage response = null;
		Exception ex = null;

		try {
			for(int i = 0; i < 11; i++) {
				key = String.valueOf(i);
				value = String.valueOf(i+1);
				kvClient.put(key,value);
			}
			response = kvClient.get("0");
			
		} catch (Exception e) {
			ex = e;
		}

		assertTrue(ex == null && response.getStatus() == StatusType.GET_SUCCESS);
	}	
	
	// Cache Tests
	@Test
	public void testFIFOCache() {
		String key;
		String value;
		
		Exception ex = null;
		KVMessage response = null;
		
		try {
			for(int i = 0; i < 11; i++) {
				key = String.valueOf(i);
				value = String.valueOf(i+1);
				kvClient.put(key,value);
			}
				
		}catch(Exception e) {
			ex = e;
		}
		
//		kvClient.
		assertTrue(ex == null && response.getStatus() == StatusType.GET_SUCCESS );
	}
	
	
}
