/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.List;

/**
 * @author miguel_ferreira
 * 
 */
public class ConsoleProcess {

	protected static final String FILE_SEPARATOR = System.getProperty("file.separator");
	protected static final String PROCESS_EXECUTION_INTERRUPTED = "Process execution interrupted.";
	protected static final String PROCESS_EXECUTION_FAILED = "Process execution failed.";
	protected static final String PROCESS_EXECUTION_FAILED_INPUT = "Process execution failed due to input error.";
	protected static final String PROCESS_EXECUTION_FAILED_OUTPUT = "Process execution failed due to output error.";
	protected static ConsoleProcessCommand command = null;
	protected ProcessBuilder processBuilder = null;
	protected Process process = null;

	/**
	 * Constructs a command line process with errors redirected to standard
	 * output.
	 * 
	 * 
	 */
	public ConsoleProcess(ConsoleProcessCommand command) {
		this.command = command.clone();
		processBuilder = new ProcessBuilder(command.getCommandArray());
		processBuilder.redirectErrorStream(true);
	}

	public int executeBathProcess() throws ConsoleException {
		try {
			process = processBuilder.start();
			return process.waitFor();
		} catch (InterruptedException e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_INTERRUPTED, e);
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED, e);
		}
	}

	public void executeProcess() throws ConsoleException {
		try {
			process = processBuilder.start();
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED, e);
		}
	}

	/**
	 * Gets the output from the process. Until the process finishes the output
	 * will always be empty.
	 * 
	 * @return the output text by the process.
	 * @throws ConsoleException
	 */
	public String getProcessOutputString() throws ConsoleException {
		try {
			return getProcessOutputFromStream(process.getInputStream());
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED, e);
		}
	}

	public InputStream getProcessOutputStream() {
		return process.getInputStream();
	}

	/**
	 * Returns the same as <code>getProcessOutput()</code> because errors are
	 * being redirected to standard output.
	 * 
	 * @return
	 * @throws ConsoleException
	 */
	public String getProcessError() throws ConsoleException {
		try {
			return getProcessOutputFromStream(process.getInputStream());
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED, e);
		}
	}

	public void setProcessInput(ConsoleProcessInput input)
			throws ConsoleException {

		try {
			pipeInputToProcess(input);
			process.getOutputStream().flush();
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED_INPUT, e);
		}
	}

	public void setProcessInput(List<ConsoleProcessInput> inputs)
			throws ConsoleException {

		loopThroughInputs(inputs);
		try {
			process.getOutputStream().flush();
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED_INPUT, e);
		}
	}

	/**
	 * @param inputs
	 * @param outputStream
	 * @throws ConsoleException
	 */
	protected void loopThroughInputs(List<ConsoleProcessInput> inputs)
			throws ConsoleException {
		for (ConsoleProcessInput input : inputs)
			pipeInputToProcess(input);
	}

	/**
	 * @param outputStream
	 * @param input
	 * @throws ConsoleException
	 */
	protected void pipeInputToProcess(ConsoleProcessInput input)
			throws ConsoleException {
		if (input.isStatic())
			pipeStaticInputToProcess(input);
		else
			pipeDynamicInputToProcess(input);
	}

	/**
	 * @param outputStream
	 * @param input
	 * @throws ConsoleException
	 */
	protected void pipeStaticInputToProcess(ConsoleProcessInput input)
			throws ConsoleException {
		PrintStream outputStream = new PrintStream(process.getOutputStream());
		try {
			writeData(input, outputStream);
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED_INPUT, e);
		}
	}

	protected void writeData(ConsoleProcessInput input,
			PrintStream outputStream) throws IOException {
		outputStream.println(input.getText());
	}

	/**
	 * @param outputStream
	 * @param input
	 * @throws ConsoleException
	 */
	protected void pipeDynamicInputToProcess(ConsoleProcessInput input)
			throws ConsoleException {

		PrintStream outputStream = new PrintStream(process.getOutputStream());
		byte[] bytes;
		try {
			bytes = input.getBytes();

			while (bytes != null) {
				writeData(input, outputStream);
				bytes = input.getBytes();
			}
		} catch (Exception e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_FAILED_INPUT, e);
		}

	}

	/**
	 * @param data
	 * @param dataStrem
	 * @throws ConsoleException
	 * @throws IOException
	 */
	protected String getProcessOutputFromStream(InputStream dataStrem)
			throws ConsoleException {
		String result = "";

		if (dataStrem != null) {
			StringBuffer text = new StringBuffer();
			getTextFromStreamToStringBuffer(dataStrem, text);
			result = text.toString();
		}

		return result;
	}

	/**
	 * @param dataStream
	 * @param textBufer
	 * @throws ConsoleException
	 * @throws IOException
	 */
	protected void getTextFromStreamToStringBuffer(
			InputStream dataStream, StringBuffer textBufer)
			throws ConsoleException {
		if (dataStream != null && textBufer != null) {
			byte[] buffer = new byte[128];
			try {
				while (dataStream.available() > 0) {
					int dataSize = readData(dataStream, buffer);
					textBufer.append(new String(buffer, 0, dataSize));
				}
			} catch (Exception e) {
				throw new ConsoleException(command.getName(),
						PROCESS_EXECUTION_FAILED_OUTPUT, e);
			}
		}
	}

	protected int readData(InputStream dataStrem, byte[] buffer)
			throws IOException {
		return dataStrem.read(buffer);
	}
	
	public String doInputOutput(String input) throws ConsoleException {
		setProcessInput(new ConsoleProcessInputText(input));
		//waitFor();
		return getProcessOutputString();
	}
	
	public InputStream doInputOutput(Reader input) throws ConsoleException {
		setProcessInput(new ConsoleProcessInputReader(input));
		waitFor();
		return getProcessOutputStream();
	}

	/**
	 * @return the exitValue
	 */
	public int getExitValue() {
		return process.exitValue();
	}

	public int waitFor() throws ConsoleException {
		try {
			return process.waitFor();
		} catch (InterruptedException e) {
			throw new ConsoleException(command.getName(),
					PROCESS_EXECUTION_INTERRUPTED, e);
		}
	}

	public void destroy() {
		process.destroy();
	}

	public boolean isFinished() {
		boolean finished = true;

		try {
			process.exitValue();
		} catch (IllegalThreadStateException e) {
			finished = false;
		}

		return finished;
	}

	public boolean isExecuting() {
		return process != null;
	}

	public PipedInputStream getPipedOutput() {
		return new PipedInputStream();
	}
}
