package uk.co.sebswebs;

import java.time.Instant;
import org.apache.commons.codec.binary.Base64;

public class TwitterScanner {
	
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
	// ...
	}
	public void run() {
	// Begin aggregating mentions. Every hour, "store" the relative change
	// (e.g. write it to System.out).
	}
	private void storeValue(TSValue value) {
	// ...
	}
	
	public static void main(String ... args) {
		byte[] encodedBytes = Base64.encodeBase64("Test".getBytes());
		System.out.println("encodedBytes " + new String(encodedBytes));
		byte[] decodedBytes = Base64.decodeBase64(encodedBytes);
		System.out.println("decodedBytes " + new String(decodedBytes));
		
//		TwitterScanner scanner = new TwitterScanner("Facebook");
//		scanner.run();
	}
}