/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author miguel_ferreira
 * 
 */
public class HolInterpreterWrapper {

	private final static String NO_HOL_FILES_SUPPLIED = "No HOL files supplied.";
	private final static String NO_HOL_EXECUTABLE_SUPPLIED = "No HOL executable supplied.";

	private final static String newLine = System.getProperty("line.separator");
	private static final String HOL_TERMINATION = "quit();" + newLine;

	public static String interpretTheory(String holExecutable,
			String vdmHolTactics, String holTheory) throws InputException {
		validateInterpretTheoryArguments(holExecutable, holTheory);
		List<CommandLineProcessInput> inputs = buildHolInputTacticsTheory(vdmHolTactics,
				holTheory, HOL_TERMINATION);
		return CommandLineTools.executeProcess(holExecutable, inputs);
	}
	
	public static String interpretTheory(String holExecutable,
			String holTheory) throws InputException {
		validateInterpretTheoryArguments(holExecutable, holTheory);
		List<CommandLineProcessInput> inputs = buildHolInputTheory(holTheory, HOL_TERMINATION);
		return CommandLineTools.executeProcess(holExecutable, inputs);
	}
	



	/**
	 * @param holExecutable
	 * @throws IllegalArgumentException
	 */
	private static void validateInterpretTheoryArguments(String holExecutable,
			String holTheory) throws InputException {
		validateHolExecutableArgument(holExecutable);
		validateHolTheoryArgument(holTheory);
	}

	/**
	 * @param holTheory
	 * @throws IllegalArgumentException
	 */
	private static void validateHolTheoryArgument(String holTheory)
			throws InputException {
		InputValidator
				.validateIsFileAndExists(holTheory, NO_HOL_FILES_SUPPLIED);
	}

	/**
	 * @param holExecutable
	 * @throws IllegalArgumentException
	 */
	private static void validateHolExecutableArgument(String holExecutable)
			throws InputException {
		InputValidator.validateStringNotEmptyNorNull(holExecutable,
				NO_HOL_EXECUTABLE_SUPPLIED);
		InputValidator.validateIsFileAndExists(holExecutable,
				NO_HOL_EXECUTABLE_SUPPLIED);
	}

	private static List<CommandLineProcessInput> buildHolInputTacticsTheory(String tactics,
			String theory, String termination) throws InputException {
		List<CommandLineProcessInput> list = buildFileReaderList(tactics,
				theory);
		list.add(new CommandLineProcessStringInput(termination));
		return list;
	}
	
	private static List<CommandLineProcessInput> buildHolInputTheory(
			String theory, String termination) throws InputException {
		List<CommandLineProcessInput> list = buildFileReaderList(theory);
		list.add(new CommandLineProcessStringInput(termination));
		return list;
	}

	private static List<CommandLineProcessInput> buildFileReaderList(
			String... args) throws InputException {
		List<CommandLineProcessInput> list = new ArrayList<CommandLineProcessInput>(
				args.length);
		for (String arg : args)
			try {
				list.add(new CommandLineProcessReaderInput(new FileReader(arg)));
			} catch (FileNotFoundException e) {
				throw new InputException("File '" + arg + "' not found.", e);
			}
		return list;
	}
}
