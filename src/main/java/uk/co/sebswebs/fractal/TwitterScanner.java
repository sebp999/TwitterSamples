package uk.co.sebswebs.fractal;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

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
	
	public void run() {
		run(new TweetReaderThread(), new LinkedList(), new ConsoleLog(), LocalDateTime.now().getHour());
	// Begin aggregating mentions. Every hour, "store" the relative change
	// (e.g. write it to System.out).

	}
	
	/**
	* Runs a thread and reads tweets from a queue.  When the hour changes, it reports the percentage change
	* of the aggregate number of tweets on the previous period.
	*/
	
	public void run(TweetReaderThread aThread, Queue<Tweet> aQueue, Log aLog, int currentHour) {
		myCurrentHour = currentHour;
		long tweetsLastHour = 0;
		long tweetsThisHour = 0;
		
		// A separate thread reads the tweets
		// Set up a queue that we can read them from
		// Pass the queue to the thread so that it can fill it up.
		
		aThread.setQueue(aQueue);
		aThread.start();
		
		Tweet currentTweet = null;
		
		while(true) {
			try {
				currentTweet = aQueue.poll();
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
			} catch (NoSuchElementException queueEmpty) {
				
				//This will probably end up being handled by the reading thread 
				//in which case empty queue means end of program.
				
				boolean successfulReconnect = aThread.reconnect();
				if (!successfulReconnect) {
					report (tweetsThisHour, tweetsLastHour, currentTweet.getTimestamp(), aLog);
					break;
				}
			}
		}
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