/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author miguel_ferreira
 *
 */
public class CommandLineProcess {

	private ProcessBuilder processBuilder = null;
	private Process process = null;
	private int exitValue = 0;
	
	/**
	 * @throws IOException 
	 * @throws InterruptedException 
	 * 
	 */
	public CommandLineProcess(List<String> command) throws IOException {
			processBuilder = new ProcessBuilder(command);
			processBuilder.redirectErrorStream(true);
	}
	
	/**
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void executeProcess() throws IOException, InterruptedException {
		process = processBuilder.start();
		exitValue = process.waitFor();
	}
	
	public String getProcessOutput() throws IOException {
		return getProcessOutputFromStream(process.getInputStream());
	}
	
	/**
	 * @param data
	 * @param dataStrem
	 * @throws IOException
	 */
	private static String getProcessOutputFromStream(InputStream dataStrem) throws IOException {
		StringBuffer text = new StringBuffer();
		getTextFromStream(dataStrem, text);
		return text.toString();
	}

	/**
	 * @param dataStrem
	 * @param text
	 * @throws IOException
	 */
	private static void getTextFromStream(InputStream dataStrem,
			StringBuffer text) throws IOException {
		byte[] buffer = new byte[128];
		while(dataStrem.available() > 0) {
			int dataSize = dataStrem.read(buffer);
			text.append(new String(buffer, 0, dataSize));
		}
	}

	/**
	 * @return the exitValue
	 */
	public int getExitValue() {
		return exitValue;
	}
}
