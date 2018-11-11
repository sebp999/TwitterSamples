package uk.co.sebswebs.fractal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

/**
 * Entry point for this project.  Reads in tweets using “GET statuses/sample” API {@link https://developer.twitter.com/en/docs/tweets/sample-realtime/overview/GET_statuse_sample}
 * and aggregates number of tweets mentioning a search term, reporting hourly. ​
 *
 */


public class TwitterScanner {
	private String mySearchTerm;
	private int myCurrentHour=0;
	private ArrayList<TSValue> myHourlyRecord = new ArrayList<TSValue>();
	
	/**
	 * A holder for timestamps and associated decimal numbers.
	 *
	 */
	
	public static class TSValue {
		
		private final Instant timestamp​​;
		private final double val;

		public TSValue(Instant timestamp, double val) {
			this.timestamp​​ = timestamp;
			this.val = val;
		}
		
		public Instant getTimestamp() {
			return timestamp​​ ;
		}
		
		public double getVal() {
			return val;
		}
	}
	
	/**
	* Constructor
	* @param companyName The word that will be searched for in tweets
	*/
	
	public TwitterScanner(String companyName) {
		mySearchTerm = companyName;
	}

	/**
	* Runs a thread and reads tweets from a queue.  When the hour changes, it reports the percentage change
	* on the previous period.
	*
	*/
	
	public void run() throws InterruptedException {
		run(new ConsoleLog(), LocalDateTime.now().getHour(), new StringToTweetParser(), new LinkedBlockingQueue<String>(100000));
	// Begin aggregating mentions. Every hour, "store" the relative change
	// (e.g. write it to System.out).
	}
	
	/**
	* Runs a thread and reads tweets from a queue.  When the hour changes, it reports the percentage change
	* of the aggregate number of tweets on the previous period.
	*/
	
	public void run(Log aLog, int currentHour, StringToTweetParser aStringToTweet, BlockingQueue<String> msgQueue) throws InterruptedException {
		
		BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);
		Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
		StatusesSampleEndpoint hosebirdEndpoint = new StatusesSampleEndpoint();

		Authentication hosebirdAuth = new OAuth1("consumerKey", "consumerSecret", "token", "secret");
		
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              // optional: mainly for the logs
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(hosebirdEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue))
				  .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

		Client hosebirdClient = builder.build();
		// Attempts to establish a connection.
		hosebirdClient.connect();

		myCurrentHour = currentHour;
		long tweetsLastHour = 0;
		long tweetsThisHour = 0;
		
		Tweet currentTweet = null;
		while(!msgQueue.isEmpty()) {
			String aTweet = msgQueue.take();
			currentTweet = aStringToTweet.convert(aTweet);
			int mentions = currentTweet.mentions(mySearchTerm);
	
			if (mentions>0) {
				if (tweetIsThisHour(currentTweet)) {
					tweetsThisHour += mentions;
				} else {
					// If the hour is not the "current hour" then an hour has passed.
					// Roll the current_hour forward and report
	
					report (tweetsThisHour, tweetsLastHour, currentTweet.getTimestamp(), aLog);
					tweetsLastHour=tweetsThisHour; 
					tweetsThisHour=0;
					tweetsThisHour += mentions;
					incrementHour();
				}
			}
		} 
		report (tweetsThisHour, tweetsLastHour, currentTweet.getTimestamp(), aLog);
	}
	
	private void incrementHour() {
		if (myCurrentHour<23) myCurrentHour++;
		else myCurrentHour=0;
	}
	
	private void report(long numberForThisHour, long numberForLastHour, Instant timestamp, Log aLog) {
		double change = 0d;
		if (numberForLastHour == 0) {
			//Percentage is based on zero so you can't report a percentage
			aLog.log("Change in mentions between "+myCurrentHour+":00 and "+(myCurrentHour==23 ? 0:myCurrentHour+1)+ ":00 zero to "+numberForThisHour, numberForLastHour, numberForThisHour);
		} else {
			//Percentage increase or decrease based on previous period
			change = (double)(numberForThisHour-numberForLastHour)/(double)numberForLastHour;
			aLog.log("Change in mentions between "+myCurrentHour+":00 and "+(myCurrentHour==23 ? 0:myCurrentHour+1)+ ":00 compared to previous hour is "+(change>=0?"+":"")+(change*100)+" %",numberForLastHour, numberForThisHour);
		}
		storeValue(new TSValue(timestamp, numberForThisHour));
	}
	
	/**
	* Returns whether tweet is in the current hour.
	*
	* @return true if the tweet is in the current hour.
	*/
	private boolean tweetIsThisHour(Tweet aTweet) {
		Instant timestamp = aTweet.getTimestamp();
		int hour = LocalDateTime.ofInstant(timestamp, ZoneId.of("UTC")).getHour();
		return hour == myCurrentHour;
	}
	
	private void storeValue(TSValue value) {
		myHourlyRecord.add(value);
	}
	
	public static void main(String ... args) {

		
//		TwitterScanner scanner = new TwitterScanner("Facebook");
//		scanner.run();
	}
}