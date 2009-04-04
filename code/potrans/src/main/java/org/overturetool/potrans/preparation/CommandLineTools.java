package org.overturetool.potrans.preparation;

import java.io.IOException;
import java.util.List;

/**
 * CommandLinesTool is the static class responsible for invoking necessary 
 * command line tools. It contains methods for:
 * <ul>
 * <li>generating Proof Obligation files for VDM++ models, using the VDMTools.
 * <li>...
 * </ul>
 * 
 * @author miguel_ferreira
 *
 */
public class CommandLineTools {
	
	public static String executeProcess(List<String> command) {
		StringBuffer result = new StringBuffer();
		
		try {
			CommandLineProcess consoleProcess = new CommandLineProcess(command);
			consoleProcess.executeProcess();
			result.append(consoleProcess.getProcessOutput());
		} catch (IOException e) {
			result.append(e.getStackTrace());
		} catch (InterruptedException e) {
			result.append(e.getStackTrace());
		}
		
		return result.toString();
	}
}
