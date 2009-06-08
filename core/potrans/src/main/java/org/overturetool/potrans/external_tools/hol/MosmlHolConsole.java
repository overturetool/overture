package org.overturetool.potrans.external_tools.hol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.overturetool.potrans.external_tools.Console;
import org.overturetool.potrans.external_tools.SystemProperties;

public class MosmlHolConsole extends Console {

	// TODO: add quit() method
	
	private String promptBuffer = null;
	
	protected final static String HOL_PROMPT = "- ";
	/**
	 * HOL initial header text is 10 lines long.
	 */
	protected final static int HEADER_SIZE = 10;
	protected boolean consoleHeaderRemoved = false;

	protected static String commandArgumentsFormat = "-quietdec -P full -I _HOLDIR_/sigobj "
			+ "_HOLDIR_/std.prelude "
			+ "_HOLDIR_/tools/unquote-init.sml "
			+ "_HOLDIR_/tools/end-init-boss.sml";

	public MosmlHolConsole(String mosmlCommand, String holDir)
			throws IOException {
		super(MosmlHolConsole.buildCommandList(mosmlCommand,
				formatCommandArguments(holDir)));
	}

	public MosmlHolConsole(String mosmlCommand, String holDir,
			Map<String, String> holEnvironment) throws IOException {
		super(buildCommandList(mosmlCommand, formatCommandArguments(holDir)),
				holEnvironment);
	}

	protected static String[] formatCommandArguments(String holDir) {
		return commandArgumentsFormat.replaceAll("_HOLDIR_", holDir).split(" ");
	}


	protected static List<String> buildCommandList(String command,
			String[] arguments) {
		ArrayList<String> list = new ArrayList<String>(arguments.length + 1);
		list.add(command);
		for (String argument : arguments)
			list.add(argument);
		return list;
	}

	public void removeConsoleHeader() throws IOException {
		if (!consoleHeaderRemoved) {
			String l;
			for (int i = 0; i < HEADER_SIZE; i++)
				l = readLine();
			readPrompt();
			consoleHeaderRemoved = true;
		}
	}
	
	protected String readPrompt() throws IOException {
		char[] cbuf = new char[2];
		output.read(cbuf);
		return new String(cbuf);
	}
	
	protected boolean isPromptNext() throws IOException {
		char[] cbuf = new char[2];
		output.read(cbuf);
		promptBuffer = new String(cbuf);
		return promptBuffer.equals(HOL_PROMPT);
	}
	
	@Override
	public String readLine() throws IOException {
		String prefix = getAndResetPromptBuffer();
		String line = super.readLine();
		return prefix != "" ? prefix + line : line;
	}
	
	private String getAndResetPromptBuffer() {
		String result = "";
		if(promptBuffer != null) {
			result = promptBuffer;
			resetPromptBuffer();
		}
		return result;
	}

	protected void resetPromptBuffer() {
		promptBuffer = null;
	}

	public String readOutputBlock() throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = null;
		while(!isPromptNext()) {
			if(line != null) {
				sb.append(SystemProperties.LINE_SEPARATOR);
			}
			line = readLine();
			sb.append(line);
		}
		resetPromptBuffer();
		return sb.toString();
	}
}
