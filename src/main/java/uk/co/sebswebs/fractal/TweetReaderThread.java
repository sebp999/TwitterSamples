package uk.co.sebswebs.fractal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import org.apache.commons.codec.binary.Base64;

public class TweetReaderThread extends Thread {
	InputStream myInputStream = null;
	HttpURLConnection myConnection = null;
	Queue<Tweet> myQueue = null;
	
//	@Override
//	public void run() {
//		super.run();
//		String username = "YOUR_USERNAME_HERE";
//		String password = "YOUR_PASSWORD_HERE";
//		String streamURL = "YOUR_STREAM_URL_HERE";
//		String charset = "UTF-8";
//
//		HttpURLConnection connection = null;
//		InputStream inputStream = null;
//
//		try {
//			connection = getConnection(streamURL, username, password);
//
//			inputStream = connection.getInputStream();
//			int responseCode = connection.getResponseCode();
//
//			if (responseCode >= 200 && responseCode <= 299) {
//
//				BufferedReader reader = new BufferedReader(
//						new InputStreamReader(new StreamingGZIPInputStream(inputStream), charset));
//				String line = reader.readLine();
//
//				while (line != null) {
//					System.out.println(line);
//					line = reader.readLine();
//				}
//			} else {
//				handleNonSuccessResponse(connection);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (connection != null) {
//				try {
//					handleNonSuccessResponse(connection);
//				} catch (IOException cantGetResponseCode) {
//					System.out.println("Non success reponse but can't access the response code/message");
//				}
//			}
//		} finally {
//			if (inputStream != null) {
//				try {
//					myInputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

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
		// TODO Auto-generated method stub
		// 
		return false;
	}
}
