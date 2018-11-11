package uk.co.sebswebs.fractal.test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;
import org.mockito.Mockito;

import uk.co.sebswebs.fractal.Log;
import uk.co.sebswebs.fractal.StringToTweetParser;
import uk.co.sebswebs.fractal.Tweet;
import uk.co.sebswebs.fractal.TwitterScanner;

public class TwitterScannerTest {
	private String getMockTimestamp (String time) {
		return LocalDateTime.parse("Tue Feb 27 "+time+" +0000 2018", DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss Z yyyy")).atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()+"";
	}

	@Test
	public void testHourChangeCausesOutput() throws InterruptedException {
		
		Log mockLog = Mockito.mock(Log.class);
		StringToTweetParser mockParser = Mockito.mock(StringToTweetParser.class);
		LinkedBlockingQueue<String> mockQueue = Mockito.mock(LinkedBlockingQueue.class);
		
		Tweet firstTweet = new Tweet("facebook tweet1", getMockTimestamp("21:30:00"));
		Tweet secondTweet = new Tweet("facebook tweet2", getMockTimestamp("22:00:00"));
		
		when(mockQueue.take()).thenReturn("").thenReturn("");
		when(mockParser.convert(Mockito.anyString())).thenReturn(firstTweet).thenReturn(secondTweet);
		when(mockQueue.isEmpty()).thenReturn(false).thenReturn(false).thenReturn(true);
		
		new TwitterScanner("facebook").run(mockLog, 21, mockParser, mockQueue);
		
		//The queue will be empty on the 3rd call, so the TwitterScanner will tell the reader to reconnect to Twitter
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 1");
		verify(mockParser, times(2)).convert(Mockito.anyString());
		verify(mockQueue, times(3)).isEmpty();
	}
	
	@Test
	public void testSeveralHours() throws InterruptedException{
		
		Log mockLog = Mockito.mock(Log.class);
		StringToTweetParser mockParser = Mockito.mock(StringToTweetParser.class);
		LinkedBlockingQueue<String> mockQueue = Mockito.mock(LinkedBlockingQueue.class);

		Tweet tweet1 = new Tweet("facebook tweet1", getMockTimestamp("21:30:00"));
		Tweet tweet2 = new Tweet("facebook tweet2", getMockTimestamp("21:30:00"));
		
		Tweet tweet3 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet4 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet5 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		Tweet tweet6 = new Tweet("facebook tweet3", getMockTimestamp("22:10:00"));
		
		Tweet tweet7 = new Tweet("facebook tweet3", getMockTimestamp("23:10:00"));
		Tweet tweet8 = new Tweet("facebook tweet3", getMockTimestamp("23:10:00"));
		
		when(mockQueue.take()).thenReturn("");
		when(mockParser.convert(Mockito.anyString())).thenReturn(tweet1).thenReturn(tweet2).thenReturn(tweet3).thenReturn(tweet4).thenReturn(tweet5).thenReturn(tweet6).thenReturn(tweet7).thenReturn(tweet8);
		when(mockQueue.isEmpty()).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(false).thenReturn(true);
		
		TwitterScanner s = new TwitterScanner("facebook");
		s.run(mockLog, 21, mockParser, mockQueue);
		
		//The queue will be empty on the 3rd call, so the TwitterScanner will tell the reader to reconnect to Twitter
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 2");
		verify(mockLog, times(1)).log("Change in mentions between 22:00 and 23:00 compared to previous hour is +100.0 %");
		verify(mockLog, times(1)).log("Change in mentions between 23:00 and 0:00 compared to previous hour is -50.0 %");
	}
	
	@Test
	public void test2MentionsIn1TweetCountsAs2() throws InterruptedException {
		
		Log mockLog = Mockito.mock(Log.class);
		StringToTweetParser mockParser = Mockito.mock(StringToTweetParser.class);
		LinkedBlockingQueue<String> mockQueue = Mockito.mock(LinkedBlockingQueue.class);
		
		Tweet firstTweet = new Tweet("facebook tweet1 facebook", getMockTimestamp("21:30:00"));
		
		when(mockQueue.take()).thenReturn("");
		when(mockParser.convert(Mockito.anyString())).thenReturn(firstTweet);
		when(mockQueue.isEmpty()).thenReturn(false).thenReturn(true);
	
		TwitterScanner s = new TwitterScanner("facebook");
		s.run(mockLog, 21, mockParser, mockQueue);
		
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 2");
		
	}
	
	@Test
	public void testHourChangeAtMidnight() throws InterruptedException {
		
		Log mockLog = Mockito.mock(Log.class);
		StringToTweetParser mockParser = Mockito.mock(StringToTweetParser.class);
		LinkedBlockingQueue<String> mockQueue = Mockito.mock(LinkedBlockingQueue.class);		

		Tweet firstTweet = new Tweet("facebook tweet1", getMockTimestamp("23:30:00"));
		Tweet secondTweet = new Tweet("facebook tweet2", getMockTimestamp("00:00:00"));
		
		when(mockQueue.take()).thenReturn("").thenReturn("");
		when(mockParser.convert(Mockito.anyString())).thenReturn(firstTweet).thenReturn(secondTweet);
		when(mockQueue.isEmpty()).thenReturn(false).thenReturn(false).thenReturn(true);
		
		TwitterScanner s = new TwitterScanner("facebook");
		s.run(mockLog, 23, mockParser, mockQueue);
		
		verify(mockLog, times(1)).log("Change in mentions between 23:00 and 0:00 zero to 1");
	}
	
	@Test
	public void testCaseInsensitive() throws InterruptedException {
		
		Log mockLog = Mockito.mock(Log.class);
		StringToTweetParser mockParser = Mockito.mock(StringToTweetParser.class);
		LinkedBlockingQueue<String> mockQueue = Mockito.mock(LinkedBlockingQueue.class);	
		
		Tweet firstTweet = new Tweet("Facebook tweet1", getMockTimestamp("21:30:00"));
		Tweet secondTweet = new Tweet("FACEBOOK tweet2", getMockTimestamp("21:30:00"));
		
		when(mockQueue.take()).thenReturn("").thenReturn("");
		when(mockParser.convert(Mockito.anyString())).thenReturn(firstTweet).thenReturn(secondTweet);
		when(mockQueue.isEmpty()).thenReturn(false).thenReturn(false).thenReturn(true);
		
		new TwitterScanner("facebook").run(mockLog, 21, mockParser, mockQueue);
		
		verify(mockLog, times(1)).log("Change in mentions between 21:00 and 22:00 zero to 2");
	}
}
