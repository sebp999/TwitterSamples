package uk.co.sebswebs.fractal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.Queue;

/**
 * A thread that will read tweets from the ​“GET statuses/sample” API and put them in a queue.
 * @author <a href="mailto:sebpalmer@gmx.com">Seb Palmer</a>
 */

public class TweetReaderThread extends Thread {
	InputStream myInputStream = null;
	HttpURLConnection myConnection = null;
	Queue<Tweet> myQueue = null;
	
    /**
     * Start the thread: open connection to Twitter endpoint, reads tweets, and puts them in the queue provided.
     */
	public void run() {
		//NOT IMPLEMENTED YET
	}

	private static void handleNonSuccessResponse(HttpURLConnection connection) throws IOException {
		int responseCode = connection.getResponseCode();
		String responseMessage = connection.getResponseMessage();
		System.out.println("Non-success response: " + responseCode + " -- " + responseMessage);
	}

	private static HttpURLConnection getConnection(String urlString, String username, String password)
			throws IOException {
		URL url = new URL(urlString);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setReadTimeout(1000 * 60 * 60);
		connection.setConnectTimeout(1000 * 10);

		connection.setRequestProperty("Authorization", createAuthHeader());
		connection.setRequestProperty("Accept-Encoding", "gzip");

		return connection;
	}

	private static String createAuthHeader() throws IOException {
		Properties loginProps = new Properties();
		loginProps.load(new FileReader(new File("loginProps.properties")));
		return "OAuth oauth_consumer_key=\""+loginProps.getProperty("consumer-key")+"\", oauth_nonce=\""+loginProps.getProperty("oauth-nonce")+"\", oauth_signature=\""+loginProps.getProperty("generated_signature")+"\", oauth_signature_method=\"HMAC-SHA1\", oauth_timestamp=\"GENERATED_TIMESTAMP\" oauth_token=\""+loginProps.getProperty("oauth-token")+"\", oauth_version=\"1.0\"";
	}

	public void setQueue(Queue aQueue) {
		myQueue = aQueue;
	}

	public boolean reconnect() {
		return false;
	}
}
