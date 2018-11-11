package uk.co.sebswebs.fractal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;

public class TwitterStatusQueue extends LinkedList {
	
	private LinkedList<Tweet> myQueue = new LinkedList<Tweet>();
	
	public TwitterStatusQueue() {
		super();
	}
	
	public Tweet poll() throws NoSuchElementException {
		try {
			return myQueue.remove(0);
		} catch (IndexOutOfBoundsException nothingInQueue) {
			throw new NoSuchElementException("Queue empty");
		}
	}
	
	public void add(String message) {
		try {
			JSONObject jsonObj = new JSONObject(message);
			//need the structure of the json to extract text and timestamp from message
		} catch (JSONException badJSON) {
			//Add nothing to queue if the message can't be parsed
		}
	}
}
