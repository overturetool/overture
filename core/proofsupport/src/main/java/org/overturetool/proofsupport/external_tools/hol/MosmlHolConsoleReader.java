package org.overturetool.proofsupport.external_tools.hol;

import java.io.IOException;

import org.overturetool.proofsupport.external_tools.BufferedConsoleReader;
import org.overturetool.proofsupport.external_tools.ConsoleReader;
import org.overturetool.proofsupport.external_tools.Utilities;

public class MosmlHolConsoleReader extends BufferedConsoleReader implements ConsoleReader {

	protected final static String HOL_PROMPT = "- ";
	private String promptBuffer = null;
	protected boolean consoleHeaderRemoved = false;
	/**
	 * HOL initial header text is 10 lines long.
	 */
	protected final static int HEADER_SIZE = 10;
	
	public MosmlHolConsoleReader() throws IOException {
		super();
	}
	
	@Override
	public String readLine() throws IOException {
		String prefix = getAndResetPromptBuffer();
		String line = super.readLine();
		return prefix != "" ? prefix + line : line;
	}
	
	public void removeConsoleHeader() throws IOException {
		if (!consoleHeaderRemoved) {
			for (int i = 0; i < HEADER_SIZE; i++)
				readLine();
			readPrompt();
			consoleHeaderRemoved = true;
		}
	}
	
	protected boolean isPromptNext() throws IOException {
		char[] cbuf = readTwoChars();
		promptBuffer = new String(cbuf);
		return promptBuffer.equals(HOL_PROMPT);
	}
	
	private char[] readTwoChars() throws IOException {
		char[] cbuf = new char[2];
		bis.read(cbuf);
		return cbuf;
	}
	
	protected String readPrompt() throws IOException {
		char[] cbuf = readTwoChars();
		return new String(cbuf);
	}
	
	private String getAndResetPromptBuffer() {
		String result = "";
		if (promptBuffer != null) {
			result = promptBuffer;
			resetPromptBuffer();
		}
		return result;
	}

	protected void resetPromptBuffer() {
		promptBuffer = null;
	}

	@Override
	public String readBlock() throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = null;
		while (!isPromptNext()) {
			if (line != null) {
				sb.append(Utilities.LINE_SEPARATOR);
			}
			line = readLine();
			sb.append(line);
		}
		resetPromptBuffer();
		return sb.toString();
	}

}
