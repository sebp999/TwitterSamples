package uk.co.sebswebs.fractal;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitterStatusQueue {
	
	private Queue<TwitterScanner.TSValue> myQueue = null;
	
	public TwitterStatusQueue() {
		super();
		myQueue = new LinkedList<TwitterScanner.TSValue>();
	}
	
	public TwitterScanner.TSValue getOneStatus() throws NoSuchElementException {
		return myQueue.remove();
	}
	
	public void addMessage(String message) {
		try {
			JSONObject jsonObj = new JSONObject(message);
		} catch (JSONException badJSON) {
			//Add nothing to queue
		}
	}
}
