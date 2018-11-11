package uk.co.sebswebs.fractal.test;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.junit.Test;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;

import uk.co.sebswebs.fractal.Log;
import uk.co.sebswebs.fractal.Tweet;
import uk.co.sebswebs.fractal.TweetReaderThread;
import uk.co.sebswebs.fractal.TwitterScanner;

public class TwitterScannerTest {
	private String getMockTimestamp (String time) {
		return LocalDateTime.parse("Tue Feb 27 "+time+" +0000 2018", DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy")).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()+"";
	}

	@Test
	public void testHourChangeCausesOutput() {
		
		LinkedList<Tweet> mockQueue = Mockito.mock(LinkedList.class);
		TweetReaderThread mockReaderThread = Mockito.mock(TweetReaderThread.class);
		Log mockLog = Mockito.mock(Log.class);

		Tweet firstTweet = new Tweet("facebook tweet1", getMockTimestamp("21:30:00"));
		Tweet secondTweet = new Tweet("facebook tweet2", getMockTimestamp("22:00:00"));
		
		when(mockQueue.poll()).thenReturn(firstTweet).thenReturn(secondTweet).thenThrow(new NoSuchElementException());
		
		when(mockReaderThread.reconnect()).thenReturn(false); //enables the run method to end
		
		new TwitterScanner("facebook").run(mockReaderThread, mockQueue, mockLog, 21);
		
		verify(mockQueue, times(3)).poll(); //2 tweets and an exception
		verify(mockReaderThread ,times(1)).start();
		
		//The queue will be empty on the 3rd call, so the TwitterScanner will tell the reader to reconnect to Twitter
		verify(mockReaderThread ,times(1)).reconnect();
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 1", 0l, 1l);
	}
	
	@Test
	public void testSeveralHours() {
		
		LinkedList<Tweet> mockQueue = Mockito.mock(LinkedList.class);
		TweetReaderThread mockReaderThread = Mockito.mock(TweetReaderThread.class);
		Log mockLog = Mockito.mock(Log.class);

		Tweet tweet1 = new Tweet("facebook tweet1", getMockTimestamp("21:30:00"));
		Tweet tweet2 = new Tweet("facebook tweet2", getMockTimestamp("21:30:00"));
		Tweet tweet3 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet4 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet5 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet6 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet7 = new Tweet("facebook tweet3", getMockTimestamp("23:10:00"));
		Tweet tweet8 = new Tweet("facebook tweet3", getMockTimestamp("23:10:00"));
		
		when(mockQueue.poll()).thenReturn(tweet1).thenReturn(tweet2).thenReturn(tweet3).thenReturn(tweet4).thenReturn(tweet5).thenReturn(tweet6).thenReturn(tweet7).thenReturn(tweet8).thenThrow(new NoSuchElementException());
		
		when(mockReaderThread.reconnect()).thenReturn(false); //enables the run method to end
		
		TwitterScanner s = new TwitterScanner("facebook");
		s.run(mockReaderThread, mockQueue, mockLog, 21);
		
		verify(mockQueue, times(9)).poll(); //2 tweets and an exception
		verify(mockReaderThread ,times(1)).start();
		
		//The queue will be empty on the 3rd call, so the TwitterScanner will tell the reader to reconnect to Twitter
		verify(mockReaderThread ,times(1)).reconnect();
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 2", 0l, 2l);
		verify(mockLog, times(1)).log("Change in mentions between 22:00 and 23:00 compared to previous hour is +100.0 %", 2l, 4l);
		verify(mockLog, times(1)).log("Change in mentions between 23:00 and 0:00 compared to previous hour is -50.0 %", 4l, 2l);
	}
	
	@Test
	public void test2MentionsIn1TweetCountsAs2() {
		
		LinkedList<Tweet> mockQueue = Mockito.mock(LinkedList.class);
		TweetReaderThread mockReaderThread = Mockito.mock(TweetReaderThread.class);
		Log mockLog = Mockito.mock(Log.class);
		
		Tweet firstTweet = new Tweet("facebook tweet1 facebook", getMockTimestamp("21:30:00"));
		
		when(mockQueue.poll()).thenReturn(firstTweet).thenThrow(new NoSuchElementException());
		when(mockReaderThread.reconnect()).thenReturn(false);
		
		new TwitterScanner("facebook").run(mockReaderThread, mockQueue, mockLog, 21);
		
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 2", 0l, 2l);
		
		//The queue will be empty on the 3rd call, so the TwitterScanner will tell the reader to reconnect to Twitter
		verify(mockReaderThread ,times(1)).reconnect();
	}
	
	@Test
	public void testHourChangeAtMidnight() {
		
		LinkedList<Tweet> mockQueue = Mockito.mock(LinkedList.class);
		TweetReaderThread mockReaderThread = Mockito.mock(TweetReaderThread.class);
		Log mockLog = Mockito.mock(Log.class);

		Tweet firstTweet = new Tweet("facebook tweet1", getMockTimestamp("23:30:00"));
		Tweet secondTweet = new Tweet("facebook tweet2", getMockTimestamp("00:00:00"));
		
		when(mockQueue.poll()).thenReturn(firstTweet).thenReturn(secondTweet).thenThrow(new NoSuchElementException());
		
		when(mockReaderThread.reconnect()).thenReturn(false); //enables the run method to end
		
		new TwitterScanner("facebook").run(mockReaderThread, mockQueue, mockLog, 23);
		
		verify(mockQueue, times(3)).poll(); //2 tweets and an exception
		verify(mockReaderThread ,times(1)).start();
		verify(mockLog, times(1)).log("Change in mentions between 23:00 and 0:00 zero to 1", 0l, 1l); //The tweet at 23:30

		//The queue will be empty on the 3rd call, so the TwitterScanner will tell the reader to reconnect to Twitter
		verify(mockReaderThread ,times(1)).reconnect();
	}
	
	@Test
	public void testCaseInsensitive() {
		
		LinkedList<Tweet> mockQueue = Mockito.mock(LinkedList.class);
		TweetReaderThread mockReaderThread = Mockito.mock(TweetReaderThread.class);
		Log mockLog = Mockito.mock(Log.class);
		
		Tweet firstTweet = new Tweet("Facebook tweet1", getMockTimestamp("21:30:00"));
		Tweet secondTweet = new Tweet("FACEBOOK tweet2", getMockTimestamp("21:30:00"));
		
		when(mockQueue.poll()).thenReturn(firstTweet).thenReturn(secondTweet).thenThrow(new NoSuchElementException());
		when(mockReaderThread.reconnect()).thenReturn(false);
		
		new TwitterScanner("facebook").run(mockReaderThread, mockQueue, mockLog, 21);
		
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 2", 0l, 2l);
	}
	
}
