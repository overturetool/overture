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

	public String readOutputBlock() throws IOException, HolInterpreterException {
		String block = output.readBlock();
		validateInterpretation();
		return block;
	}

	private void validateInterpretation() throws IOException, HolInterpreterException {
		String errorMessage = null;
		if(error.ready())
			errorMessage = readAllErrorLines();
		if(errorMessage != null) 
			throw new HolInterpreterException("Interpretation failed." + errorMessage);
	}

	public void quitHol() throws InterruptedException {
		writeLine(HOL_QUIT_COMMAND);
		waitFor();
	}
}
