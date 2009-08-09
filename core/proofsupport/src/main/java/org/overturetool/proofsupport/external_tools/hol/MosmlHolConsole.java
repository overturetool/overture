package org.overturetool.proofsupport.external_tools.hol;

import java.io.IOException;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Console;

public class MosmlHolConsole extends Console {

	protected final static String HOL_QUIT_COMMAND = "quit();";
	protected final static String HOL_PROMPT = "- ";
	/**
	 * HOL initial header text is 10 lines long.
	 */
	protected final static int HEADER_SIZE = 10;
	protected boolean consoleHeaderRemoved = false;

	public MosmlHolConsole(List<String> mosmlCmd) throws IOException {
		super(mosmlCmd, new MosmlHolConsoleReader());
		
	}

//	public void removeConsoleHeader() throws IOException {
//		if (!consoleHeaderRemoved) {
//			for (int i = 0; i < HEADER_SIZE; i++)
//				readLine();
//			readPrompt();
//			consoleHeaderRemoved = true;
//		}
//	}

//	protected String readPrompt() throws IOException {
//		char[] cbuf = readTwoChars();
//		return new String(cbuf);
//	}

//	private char[] readTwoChars() throws IOException {
//		char[] cbuf = new char[2];
//		output.read(cbuf);
//		return cbuf;
//	}

//	protected boolean isPromptNext() throws IOException {
//		char[] cbuf = readTwoChars();
//		promptBuffer = new String(cbuf);
//		return promptBuffer.equals(HOL_PROMPT);
//	}

//	@Override
//	public String readLine() throws IOException {
//		String prefix = getAndResetPromptBuffer();
//		String line = super.readLine();
//		return prefix != "" ? prefix + line : line;
//	}

//	private String getAndResetPromptBuffer() {
//		String result = "";
//		if (promptBuffer != null) {
//			result = promptBuffer;
//			resetPromptBuffer();
//		}
//		return result;
//	}

//	protected void resetPromptBuffer() {
//		promptBuffer = null;
//	}

	public String readOutputBlock() throws IOException {
		return output.readBlock();
//		StringBuffer sb = new StringBuffer();
//		String line = null;
//		while (!isPromptNext()) {
//			if (line != null) {
//				sb.append(Utilities.LINE_SEPARATOR);
//			}
//			line = readLine();
//			sb.append(line);
//		}
//		resetPromptBuffer();
//		return sb.toString();
	}

	public void quitHol() throws InterruptedException {
		writeLine(HOL_QUIT_COMMAND);
		waitFor();
	}
}
