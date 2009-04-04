/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miguel_ferreira
 * 
 */
public class VdmToolsWrapper {
	
	private static final String VDMTOOLS_POG_FLAG = "-g";
	private static final String NO_VDM_FILES_SUPPLIED = "No VDM files supplied.";
	private static final String NO_VDMTOOLS_EXECUTABLE_SUPPLIED = "No VDMTools executable supplied.";

	public static String generatePogFile(String vppdeExecutable,
			String[] vdmFiles) throws IllegalArgumentException {
		validateGeneratePogFileArguments(vppdeExecutable, vdmFiles);
		List<String> command = buildPogComand(vppdeExecutable, vdmFiles);
		return CommandLineTools.executeProcess(command);
	}

	/**
	 * @param vppdeExecutable
	 * @param vdmFiles
	 * @param commandSize
	 * @return
	 */
	private static List<String> buildPogComand(String vppdeExecutable,
			String[] vdmFiles) {
		return buildFlggedCommand(vppdeExecutable, VDMTOOLS_POG_FLAG, vdmFiles);
	}

	/**
	 * @param vppdeExecutable
	 * @param vdmFiles
	 * @param command
	 */
	private static List<String> buildFlggedCommand(String vppdeExecutable,
			String flag, String[] vdmFiles) {
		ArrayList<String> command = new ArrayList<String>(vdmFiles.length + 2);
		command.add(vppdeExecutable);
		command.add(flag);
		addFilesToListAtOffset(vdmFiles, command, 2);

		return command;
	}

	/**
	 * @param vdmFiles
	 * @param command
	 */
	private static void addFilesToListAtOffset(String[] vdmFiles,
			ArrayList<String> command, int offset) {
		for (int i = 0; i < vdmFiles.length; i++)
			command.add(i + offset, vdmFiles[i]);
	}

	/**
	 * @param vppdeExecutable
	 * @param vdmFiles
	 * @throws IllegalArgumentException
	 */
	private static void validateGeneratePogFileArguments(
			String vppdeExecutable, String[] vdmFiles)
			throws IllegalArgumentException {
		validateVdmFilesArgument(vdmFiles);
		validateVppdeExectuableArgument(vppdeExecutable);
	}

	/**
	 * @param vppdeExecutable
	 * @throws IllegalArgumentException
	 */
	private static void validateVppdeExectuableArgument(String vppdeExecutable)
			throws IllegalArgumentException {
		InputValidator.validateStringNotEmptyNorNull(vppdeExecutable,
				NO_VDMTOOLS_EXECUTABLE_SUPPLIED);
		InputValidator.validateIsFileAndExists(vppdeExecutable,
				NO_VDMTOOLS_EXECUTABLE_SUPPLIED);
	}

	/**
	 * @param vdmFiles
	 * @throws IllegalArgumentException
	 */
	private static void validateVdmFilesArgument(String[] vdmFiles)
			throws IllegalArgumentException {
		InputValidator.validateAtLeastOneFile(vdmFiles, NO_VDM_FILES_SUPPLIED);
	}
}
