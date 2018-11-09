package uk.co.sebswebs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class StreamingGZIPInputStream extends GZIPInputStream {
	private final InputStream wrapped;
	
	public StreamingGZIPInputStream(InputStream in) throws IOException {
		super(in);
		wrapped=in;
	}

	@Override
	public int available() throws IOException {
		return wrapped.available();
	}
}
