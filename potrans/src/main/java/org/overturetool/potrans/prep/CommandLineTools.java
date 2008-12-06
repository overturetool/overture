package org.overturetool.potrans.prep;

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
	
	/**
	 * Calls the VDMTools command line to generate a .pog file. 
	 * If the call to the VDMTools is successful it should create a file 
	 * containing the Proof Obligations, named after the first file in the 
	 * {@link FILENAMES} array and suffixed with the string ".pog".
	 * 
	 * @param  fileNames the VDM++ models' file paths
	 * @param  vppde     the VDMToolbox command line path
	 * @return the value returned by the process that executes the command
	 * @throws CommandLineException If an exception occurred within the 
	 *                              execution of the command. 
	 */
	public static int genPogFile(String[] fileNames, String vppde) throws CommandLineException {
		// build the command string
		StringBuffer sb = new StringBuffer();
		sb.append(vppde);
		sb.append(" -g ");
		for(int i = 0; i < fileNames.length; i++) {
			sb.append(fileNames[i]);
		}
		
		// execute the VDMTools with the -g option
		Runtime rt = Runtime.getRuntime();
		Process proc;
		try {
			// if successful the VDMTools will output a "fileName".pog
			proc = rt.exec(sb.toString());
			int val = proc.waitFor();
			
//			// dump process output and error messages to System
//			printStream(proc.getInputStream(), System.out);
//			printStream(proc.getErrorStream(), System.err);
			
			return val;
		} catch (IOException e) {
			throw new CommandLineException(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new CommandLineException(e.getMessage(), e);
		}
	}

	/**
	 * Prints data from an InputStream and writes it to a PrintStream.
	 * @param is Stream containing the data.
	 * @param ps Stream where to write the data.
	 * @throws IOException
	 */
	private static void printStream(InputStream is, PrintStream ps) throws IOException {
		byte[] error = new byte[1024];
		while(is.available() > 0) {
			is.read(error, 0, error.length);
			ps.println(new String(error));
		}
	}


}
