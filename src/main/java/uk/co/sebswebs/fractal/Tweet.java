package uk.co.sebswebs.fractal;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a single tweet from a Twitter feed.  Contains only the timestamp and text of the tweet.
 *
 */

public class Tweet {
	
	private String myMessage;
	private String myTimestamp;
	
	public Tweet(String aMessage, String aTimestamp) {
		myTimestamp = aTimestamp;
		myMessage = aMessage;
	}
	
	public Instant getTimestamp() {
		return Instant.ofEpochMilli(Long.parseLong(myTimestamp));
	}
	/**
	* The number of times the search term appears in this Tweet.
	* @param aSearchTerm The search term
	* @return The number of mentions of the search term 
	*/
	public int mentions(String aSearchTerm) {
		return StringUtils.countMatches(myMessage.toLowerCase(), aSearchTerm.toLowerCase());
	}
}
