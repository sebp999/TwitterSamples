package uk.co.sebswebs.fractal;

/**
 * Simple Logging implemetation
 *
 */

public class ConsoleLog implements Log {
	/**
	* Logs a message to System.out
	* @param logMessage The message to be logged
	*/
	
	@Override
	public void log(String logMessage) {
		System.out.println(logMessage);
	}
}
