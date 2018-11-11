package uk.co.sebswebs.fractal;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Base64;

public class TwitterScanner {
	private int currentHourNumber;
	private TwitterStatusQueue myQueue = null;
	private String mySearchTerm;
	private int myCurrentHour=0;
	private ArrayList myHourlyRecord = new ArrayList<HashMap.Entry<LocalDateTime, Long>>();
	
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
	
	public TwitterScanner(String companyName) {
		mySearchTerm = companyName;
	}
	public void run() {
		run(new TweetReaderThread(), new LinkedList(), new ConsoleLog(), LocalDateTime.now().getHour());
	// Begin aggregating mentions. Every hour, "store" the relative change
	// (e.g. write it to System.out).
	
	
	//Set up a thread that reads the tweets
	//Set up a queue that we can read them from
	//Pass the queue to the thread so that it can fill it up.
		
	//Read from the queue
	//If the queue is empty or if there is a disconnect message then make a new connection, give it the queue
	//and try again
	//If the hour is not the "current hour" then roll the current_hour forward and report
	}
	public void run(TweetReaderThread aThread, LinkedList<Tweet> aQueue, Log aLog, int currentHour) {
		myCurrentHour = currentHour;
		long tweetsLastHour = 0;
		long tweetsThisHour = 0;
		aThread.setQueue(aQueue);
		aThread.start();
		
		Tweet currentTweet = null;
		
		while(true) {
			try {
				currentTweet = aQueue.poll();
				int mentions = currentTweet.mentions(mySearchTerm);
				
				if (mentions>0) {
					if (tweetIsThisHour(currentTweet)) {
						tweetsThisHour+=mentions;
					} else {
						report (tweetsThisHour, tweetsLastHour, currentTweet.getTimestamp(), aLog);
						tweetsLastHour=tweetsThisHour; 
						tweetsThisHour=0;
						tweetsThisHour+=mentions;
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
			aLog.log("Change in mentions between "+myCurrentHour+":00 and "+(myCurrentHour==23 ? 0:myCurrentHour+1)+ ":00 zero to "+numberForThisHour, numberForLastHour, numberForThisHour);
		} else {
			change = (double)(numberForThisHour-numberForLastHour)/(double)numberForLastHour;
			aLog.log("Change in mentions between "+myCurrentHour+":00 and "+(myCurrentHour==23 ? 0:myCurrentHour+1)+ ":00 compared to previous hour is "+(change>=0?"+":"")+(change*100)+" %",numberForLastHour, numberForThisHour);
		}
		storeValue(new TSValue(timestamp, numberForThisHour));
	}
	
	private boolean tweetIsThisHour(Tweet aTweet) {
		Instant timestamp = aTweet.getTimestamp();
		int hour = LocalDateTime.ofInstant(timestamp, ZoneId.of("UTC")).getHour();
		return hour == myCurrentHour;
	}
	
	private void storeValue(TSValue value) {
		myHourlyRecord.add(null);
	}
	
	public static void main(String ... args) {

		
//		TwitterScanner scanner = new TwitterScanner("Facebook");
//		scanner.run();
	}
}