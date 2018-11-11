package uk.co.sebswebs.fractal;

public class ConsoleLog implements Log {

	@Override
	public void log(String logMessage, long lastNumber, long thisNumber) {
		System.out.println(logMessage);
	}
}
