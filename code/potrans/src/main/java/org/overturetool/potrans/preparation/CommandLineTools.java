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

	public static String executeProcess(String command, String[] arguments) throws CommandLineException {
		CommandLineProcessOutput processOutput = new CommandLineProcessOutput();
		
		try {
			processOutput.appendOutput(executeBatchProcess(command, arguments));
		} catch (Exception e) {
			throw new CommandLineException(command, e.getMessage(), e);
		}
		
		return processOutput.getOutput();
	}


	/**
	 * @param command
	 * @param arguments
	 * @param result
	 * @throws IOException
	 * @throws InterruptedException
	 */
	protected static String executeBatchProcess(String command,
			String[] arguments) throws IOException,
			InterruptedException {
		CommandLineProcess cmdLineProcess = new CommandLineProcess(
				new CommandLineProcessCommand(command, arguments));
		cmdLineProcess.executeProcessAndWaitForItToFinish();
		return cmdLineProcess.getProcessOutput();
	}
	
	public static String executeProcess(String commandName, String[] arguments, List<CommandLineProcessInput> inputs) {
		CommandLineProcessOutput processOutput = new CommandLineProcessOutput();
		CommandLineProcess cmdLineProcess = null;
		
		try {
			cmdLineProcess = executeInteractiveProcess(commandName, arguments,
					inputs);
			processOutput.appendOutput(cmdLineProcess.getProcessOutput());
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
	protected static CommandLineProcess executeInteractiveProcess(
			String commandName, String[] arguments,
			List<CommandLineProcessInput> inputs) throws IOException,
			InterruptedException {
		CommandLineProcess cmdLineProcess;
		cmdLineProcess = new CommandLineProcess(new CommandLineProcessCommand(commandName, arguments));
		cmdLineProcess.executeProcess();
		cmdLineProcess.setProcessInput(inputs);
		cmdLineProcess.waitFor();
		return cmdLineProcess;
	}
	
	public static String executeProcess(String commandName, List<CommandLineProcessInput> inputs) {
		return executeProcess(commandName, null, inputs);
	}
}
