package org.overturetool.proofsupport.external_tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Console {

	protected final static int WAIT_SECCONDS = 2000;
	protected final Process process;
	protected final PrintWriter input;
	protected final BufferedReader output;
	protected final BufferedReader error;

	public Console(List<String> command) throws IOException {
		process = startProcess(command, null);
		input = new PrintWriter(process.getOutputStream());
		output = new BufferedReader(new InputStreamReader(process
				.getInputStream()));
		error = new BufferedReader(new InputStreamReader(process
				.getErrorStream()));
	}

	protected Process startProcess(List<String> command,
			Map<String, String> commandEnvironment) throws IOException {
		ProcessBuilder processBuilder = initProcessBuilder(command,
				commandEnvironment);
		return processBuilder.start();
	}

	protected ProcessBuilder initProcessBuilder(List<String> command,
			Map<String, String> commandEnvironment) {
		ProcessBuilder processBuilder;
		if (commandEnvironment != null)
			processBuilder = addCommandVariablesToEnvironment(command,
					commandEnvironment);
		else
			processBuilder = new ProcessBuilder(command);
		return processBuilder;
	}

	protected ProcessBuilder addCommandVariablesToEnvironment(
			List<String> command, Map<String, String> commandEnvironment) {
		ProcessBuilder processBuilder;
		processBuilder = new ProcessBuilder(command);
		Map<String, String> environment = processBuilder.environment();
		for (String key : commandEnvironment.keySet())
			addCommandVariableToEnvironment(commandEnvironment, environment,
					key);
		return processBuilder;
	}

	protected void addCommandVariableToEnvironment(
			Map<String, String> commandEnvironment,
			Map<String, String> environment, String key) {
		if (!environment.containsKey(key))
			environment.put(key, commandEnvironment.get(key));
		else
			appendValueToVariable(commandEnvironment, environment, key);
	}

	protected void appendValueToVariable(
			Map<String, String> commandEnvironment,
			Map<String, String> environment, String key) {
		String previousValue = environment.get(key);
		String newValue = commandEnvironment.get(key)
				+ Utilities.PATH_SEPARATOR + previousValue;
		environment.put(key, newValue);
	}

	public void writeLine() {
		input.println();
		input.flush();
	}

	public void writeLine(String line) {
		input.println(line);
		input.flush();
	}

	public void writeLines(String[] lines) {
		for (String line : lines)
			input.println(line);
		input.flush();
	}

	/**
	 * Writes a line to process input and reads a line of process output.
	 * Calling this method eventually results in an invocation of
	 * <code>BufferedReder.realLine()</code> that in principle blocks the
	 * current thread if no text is available, or returns null if the underlying
	 * stream is closed.
	 * 
	 * @return a line of text or null if the stream is closed
	 * @throws IOException
	 */
	public String writeAndReadLine(String line) throws IOException {
		writeLine(line);
		return readLine();
	}

	/**
	 * Reads a line of process output. Calling this method results in an
	 * invocation of <code>BufferedReder.realLine()</code> that in principle
	 * blocks the current thread if no text is available, or returns null if the
	 * underlying stream is closed.
	 * 
	 * @return a line of text or null if the stream is closed
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		return output.readLine();
	}

	/**
	 * Reads all lines of process output. Calling this method results in a
	 * sequence of invocations of <code>BufferedReder.realLine()</code> until
	 * the end of the underlying stream is reached. If this method is called
	 * before the process has terminated the caller tread will block until the
	 * process terminates.
	 * 
	 * @return all lines of text from process output.
	 * @throws IOException
	 */
	public String readAllLines() throws IOException {
		StringBuffer sb = new StringBuffer();
		String line = "";
		while ((line = readLine()) != null) {
			sb.append(line).append(Utilities.LINE_SEPARATOR);
		}
		// remove the last LINE_SEPARATOR
		sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}

	/**
	 * Reads a line of process error. Calling this method results in an
	 * invocation of <code>BufferedReder.realLine()</code> that in principle
	 * blocks the current thread if no text is available, or returns null if the
	 * stream is closed.
	 * 
	 * @return a line of text or null if the stream is closed
	 * @throws IOException
	 */
	public String readErrorLine() throws IOException {
		return error.readLine();
	}

	public void closeInput() throws IOException {
		input.close();
	}

	public void destroy() {
		process.destroy();
	}

	public int exitValue() {
		return process.exitValue();
	}

	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}

	protected static List<String> buildCommandList(String command) {
		ArrayList<String> list = new ArrayList<String>(1);
		list.add(command);
		return list;
	}

	protected static void printCommand(List<String> command) {
		for (String arg : command)
			System.out.print(arg + " ");
		System.out.println();
	}

	public boolean hasOutput() throws IOException {
		return process.getInputStream().available() > 0;
	}

	public boolean hasTerminated() {
		boolean result = true;
		try {
			process.exitValue();
		} catch (IllegalThreadStateException e) {
			result = false;
		}
		return result;
	}
}
