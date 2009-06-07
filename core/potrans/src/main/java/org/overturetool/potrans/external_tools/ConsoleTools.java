package org.overturetool.potrans.external_tools;


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
public class ConsoleTools {

	public static String executeProcess(String command, String[] arguments) throws ConsoleException {
		ConsoleProcessOutput processOutput = new ConsoleProcessOutput();
		
		try {
			processOutput.appendOutput(executeBatchProcess(command, arguments));
		} catch (Exception e) {
			throw new ConsoleException(command, e.getMessage(), e);
		}
		
		return processOutput.getOutput();
	}


	/**
	 * @param command
	 * @param arguments
	 * @param result
	 * @throws ConsoleException 
	 */
	protected static String executeBatchProcess(String command,
			String[] arguments) throws ConsoleException {
		ConsoleProcess cmdLineProcess = new ConsoleProcess(
				new ConsoleProcessCommand(command, arguments));
		cmdLineProcess.executeBathProcess();
		return cmdLineProcess.getProcessOutputString();
	}
	
	public static String executeProcess(String commandName, String[] arguments, List<ConsoleProcessInput> inputs) {
		ConsoleProcessOutput processOutput = new ConsoleProcessOutput();
		ConsoleProcess cmdLineProcess = null;
		
		try {
			cmdLineProcess = executeInteractiveProcess(commandName, arguments,
					inputs);
			processOutput.appendOutput(cmdLineProcess.getProcessOutputString());
		} catch (Exception e) {
			processOutput.appendErrorTrace(e);
		} finally {
			cmdLineProcess.destroy();
		}
		
		return processOutput.getOutput();
	}

	/**
	 * @param commandName
	 * @param arguments
	 * @param inputs
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected static ConsoleProcess executeInteractiveProcess(
			String commandName, String[] arguments,
			List<ConsoleProcessInput> inputs) throws ConsoleException {
		ConsoleProcess cmdLineProcess;
		cmdLineProcess = new ConsoleProcess(new ConsoleProcessCommand(commandName, arguments));
		cmdLineProcess.executeProcess();
		cmdLineProcess.setProcessInput(inputs);
		cmdLineProcess.waitFor();
		return cmdLineProcess;
	}
	
	public static String executeProcess(String commandName, List<ConsoleProcessInput> inputs) {
		return executeProcess(commandName, null, inputs);
	}
}
