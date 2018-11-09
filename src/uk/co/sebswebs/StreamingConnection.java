package uk.co.sebswebs;

import java.io.*;
import java.lang.String;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;

public class StreamingConnection {
	public static void main(String... args) throws IOException {

		String username = "YOUR_USERNAME_HERE";
		String password = "YOUR_PASSWORD_HERE";
		String streamURL = "YOUR_STREAM_URL_HERE";
		String charset = "UTF-8";

		HttpURLConnection connection = null;
		InputStream inputStream = null;

		try {
			connection = getConnection(streamURL, username, password);

			inputStream = connection.getInputStream();
			int responseCode = connection.getResponseCode();

			if (responseCode >= 200 && responseCode <= 299) {

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new StreamingGZIPInputStream(inputStream), charset));
				String line = reader.readLine();

				while (line != null) {
					System.out.println(line);
					line = reader.readLine();
				}
			} else {
				handleNonSuccessResponse(connection);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (connection != null) {
				handleNonSuccessResponse(connection);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
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

		connection.setRequestProperty("Authorization", createAuthHeader(username, password));
		connection.setRequestProperty("Accept-Encoding", "gzip");

		return connection;
	}

	private static String createAuthHeader(String username, String password) throws UnsupportedEncodingException {
		String authToken = username + ":" + password;
		return "Basic " + Base64.encodeBase64(authToken.getBytes());
	}
}