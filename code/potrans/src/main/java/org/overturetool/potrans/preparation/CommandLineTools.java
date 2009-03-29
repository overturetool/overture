package org.overturetool.potrans.preparation;

import java.io.*;

/**
 * CommandLinesTool is the static class responsible for invoking necessary 
 * command line tools. It contains methods for:
 * <ul>
 * <li>generating Proof Obligation files for VDM++ models, using the VDMTools.
 * <li>...
 * </ul>
 * 
 * @author Miguel Ferreira
 *
 */
public class CommandLineTools {
	
	public static String newLine = System.getProperty("line.separator");
	public static String fileSeparator = System.getProperty("file.separator");
	public static String userDir = System.getProperty("user.dir");
	
	public static String executeProcess(String cmdText) {
		StringBuffer result = new StringBuffer();
		
		try {
			// execute command
			Process process = Runtime.getRuntime().exec(cmdText);
			int exitValue = process.waitFor();
			
			collectProcessOutput(result, process);
			
			if(exitValue != 0) {
				result.append("Abnormal termination with exit value <"
						+ exitValue + ">, and error message:" + newLine);
				collectProcessError(result, process);
			}
		} catch (IOException e) {
			result.append(e.getStackTrace());
		} catch (InterruptedException e) {
			result.append(e.getStackTrace());
		}
		
		return result.toString();
	}


	/**
	 * @param buffer
	 * @param process
	 * @throws IOException
	 */
	private static void collectProcessOutput(StringBuffer buffer,
			Process process) throws IOException {
		InputStream output = process.getInputStream();
		getDataFromStream(buffer, output);
	}
	
	/**
	 * @param buffer
	 * @param process
	 * @throws IOException
	 */
	private static void collectProcessError(StringBuffer buffer,
			Process process) throws IOException {
		InputStream error = process.getErrorStream();
		getDataFromStream(buffer, error);
	}


	/**
	 * @param data
	 * @param output
	 * @throws IOException
	 */
	private static void getDataFromStream(StringBuffer data,
			InputStream output) throws IOException {
		byte[] buffer = new byte[128];
		while(output.available() > 0) {
			int dataSize = output.read(buffer);
			data.append(new String(buffer, 0, dataSize));
		}
	}
	
	
	public static String generatePogFile(String[] vdmFiles, String vppdeExecutable)
		   throws IllegalArgumentException {
		// validate arguments
		validateVdmFilesArgument(vdmFiles);
		validateVppdeExectuableArgument(vppdeExecutable);
		
		// build command string
		StringBuffer sb =  new StringBuffer();
		for(int i = 0; i < vdmFiles.length; i++) {
			sb.append(vdmFiles[i]).append(' ');
		}
		String cmdText = vppdeExecutable + " -g " + sb.toString();
		
		// execute command
		return executeProcess(cmdText);
	}


	/**
	 * @param vppdeExecutable
	 * @throws IllegalArgumentException
	 */
	private static void validateVppdeExectuableArgument(String vppdeExecutable)
			throws IllegalArgumentException {
		Utils.validateStringNotEmptyNorNull(vppdeExecutable, "No VDMTools executable supplied.");
		
		Utils.validateIsFileAndExists(vppdeExecutable, "No VDMTools executable supplied.");
	}

	/**
	 * @param vdmFiles
	 * @throws IllegalArgumentException
	 */
	private static void validateVdmFilesArgument(String[] vdmFiles)
			throws IllegalArgumentException {
		Utils.validateAtLeastOneFile(vdmFiles, "No VDM files supplied.");
	}
}
