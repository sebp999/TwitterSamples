package uk.co.sebswebs.fractal.test;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.NoSuchElementException;

import org.junit.Test;

import uk.co.sebswebs.fractal.Tweet;
import uk.co.sebswebs.fractal.TwitterScanner.TSValue;
import uk.co.sebswebs.fractal.TwitterStatusQueue;

import org.apache.commons.io.IOUtils;

public class TwitterStatusQueueTest {

	private String getText(String fileName) throws IOException {
		InputStream is = new FileInputStream(fileName);
		return IOUtils.toString(is, StandardCharsets.UTF_8);
	}

////	@Test
//	public void testUnpackJSON() throws IOException{
//		TwitterStatusQueue aQueue = new TwitterStatusQueue();
//		aQueue.addMessage(getText("singe_tweet.json"));
//		TSValue singleTweet = aQueue.getOneStatus();
//		assertEquals(singleTweet.getTimestamp(), );
////	}
	
//	@Test
//	public void testUnpackJSON() {
//		fail("Not yet implemented");
//	}
	@Test
	public void testUnpackBadlyFormedJSONDoesntAddToQueue() throws IOException {
		TwitterStatusQueue aQueue = new TwitterStatusQueue();
		aQueue.add(getText("singe_tweet.json"));
		try {
			Tweet singleTweet = aQueue.poll();
			fail("Should have thrown NoSuchElementException because nothing in queue");
		} catch (NoSuchElementException passes) {
			//Test passes
		}
	}
}
