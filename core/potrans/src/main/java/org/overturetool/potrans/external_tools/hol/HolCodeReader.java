package org.overturetool.potrans.external_tools.hol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.overturetool.potrans.external_tools.Utilities;

public class HolCodeReader extends BufferedReader {

	protected final static String HOL_LINE_SEPARATOR = ";";
	
	protected StringBuffer holLineBuffer = new StringBuffer();
	
	public HolCodeReader(Reader in) {
		super(in);
	}

	public HolCodeReader(Reader in, int sz) {
		super(in, sz);
	}
	
	@Override
	public String readLine() throws IOException {
		readHolLine();
		return cleanBuffer();
	}

	private void readHolLine() throws IOException {
		String line = readTrimmedLine();
		while (isLineToBuffer(line)) {
			if(!Utilities.isEmptyString(line))
				holLineBuffer.append(line).append(" ");
			line = readTrimmedLine();
		}
		if(line != null)
			holLineBuffer.append(line);
	}

	private String readTrimmedLine() throws IOException {
		String line = super.readLine();
		if(line != null)
			line = line.trim();
		return line;
	}

	private boolean isLineToBuffer(String line) {
		return line != null && !line.endsWith(HOL_LINE_SEPARATOR);
	}

	private String cleanBuffer() {
		String bufferContents = null;
		if(!(isBufferEmpty())) {
			bufferContents = holLineBuffer.toString();
			holLineBuffer = new StringBuffer();
		}
		return bufferContents;
	}

	protected boolean isBufferEmpty() {
		return holLineBuffer.length() == 0;
	}
}
