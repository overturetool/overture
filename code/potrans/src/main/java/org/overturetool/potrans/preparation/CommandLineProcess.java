/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author miguel_ferreira
 * 
 */
public class CommandLineProcess {

	protected ProcessBuilder processBuilder = null;
	protected Process process = null;

	/**
	 * Constructs a command line process with errors redirected to standard output.
	 * @throws IOException
	 * @throws InterruptedException
	 * 
	 */
	public CommandLineProcess(CommandLineProcessCommand command) throws IOException {
		processBuilder = new ProcessBuilder(command.getCommandArray());
		processBuilder.redirectErrorStream(true);
	}

	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public int executeProcessAndWaitForItToFinish() throws IOException, InterruptedException {
		process = processBuilder.start();
		return process.waitFor();
	}
	
	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void executeProcess() throws IOException {
		process = processBuilder.start();
	}

	/**
	 * Gets the output from the process. Until the process finishes the output will always be empty. 
	 * @return the output text by the process.
	 * @throws IOException
	 */
	public String getProcessOutput() throws IOException {
		return getProcessOutputFromStream(process.getInputStream());
	}
	
	/**
	 * Returns the same as <code>getProcessOutput()</code> because
	 * errors are being redirected to standard output.
	 * @return
	 * @throws IOException
	 */
	public String getProcessError() throws IOException {
		return getProcessOutputFromStream(process.getInputStream());
	}

	public void setProcessInput(CommandLineProcessInput input) throws IOException {
		pipeInputToProcess(input);
		process.getOutputStream().flush();
	}
	
	public void setProcessInput(List<CommandLineProcessInput> inputs) throws IOException {
		loopThroughInputs(inputs);
		process.getOutputStream().flush();
	}

	/**
	 * @param inputs
	 * @param outputStream
	 * @throws IOException
	 */
	private void loopThroughInputs(List<CommandLineProcessInput> inputs) throws IOException {
		for(CommandLineProcessInput input : inputs)
			pipeInputToProcess(input);
	}

	/**
	 * @param outputStream
	 * @param input
	 * @throws IOException
	 */
	private void pipeInputToProcess(CommandLineProcessInput input) throws IOException {
		if(input.isStatic())
			pipeStaticInputToProcess(input);
		else
			pipeDynamicInputToProcess(input);
	}

	/**
	 * @param outputStream
	 * @param input
	 * @throws IOException
	 */
	private void pipeStaticInputToProcess(CommandLineProcessInput input) throws IOException {
		OutputStream outputStream = process.getOutputStream();
		outputStream.write(input.getBytes());
	}

	/**
	 * @param outputStream
	 * @param input
	 * @throws IOException
	 */
	private void pipeDynamicInputToProcess(CommandLineProcessInput input) throws IOException {
		OutputStream outputStream = process.getOutputStream();
		byte[] bytes = input.getBytes();
		while(bytes != null) {
			outputStream.write(bytes);
			bytes = input.getBytes();
		}
	}
	
	/**
	 * @param data
	 * @param dataStrem
	 * @throws IOException
	 */
	protected static String getProcessOutputFromStream(InputStream dataStrem)
			throws IOException {
		String result = "";

		if (dataStrem != null) {
			StringBuffer text = new StringBuffer();
			getTextFromStreamToStringBuffer(dataStrem, text);
			result = text.toString();
		}

		return result;
	}

	/**
	 * @param dataStrem
	 * @param textBufer
	 * @throws IOException
	 */
	protected static void getTextFromStreamToStringBuffer(InputStream dataStrem,
			StringBuffer textBufer) throws IOException {
		if (dataStrem != null && textBufer != null) {
			byte[] buffer = new byte[128];
			while (dataStrem.available() > 0) {
				int dataSize = dataStrem.read(buffer);
				textBufer.append(new String(buffer, 0, dataSize));
			}
		}
	}

	/**
	 * @return the exitValue
	 */
	public int getExitValue() {
		return process.exitValue();
	}
	
	public int waitFor() throws InterruptedException {
		return process.waitFor();
	}
	
	public void destroy() {
		process.destroy();
	}
	
	public boolean isFinished() {
		boolean finished = true;
		
		try {
			process.exitValue();
		} catch(IllegalThreadStateException e) {
			finished = false;
		}
		
		return finished;
	}

}
