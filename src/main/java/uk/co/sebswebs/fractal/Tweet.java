package uk.co.sebswebs.fractal;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;


public class Tweet {
	
	private String myMessage;
	private String myTimestamp;
	
	public Tweet(String aMessage, String aTimestamp) {
		myTimestamp = aTimestamp;
		myMessage = aMessage;
	}
	
	public boolean isRelevant(String term) {
		return myMessage.contains(term);
	}

	public Instant getTimestamp() {
		return Instant.ofEpochMilli(Long.parseLong(myTimestamp));
	}

	public int mentions(String aSearchTerm) {
		return StringUtils.countMatches(myMessage.toLowerCase(), aSearchTerm.toLowerCase());
	}
}
