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
		String[] arguments = buildFlaggedPogComamndArguments(vdmFiles);
		return CommandLineTools.executeProcess(vppdeExecutable, arguments);
	}



	/**
	 * @param vdmFiles
	 * @return
	 */
	private static String[] buildFlaggedPogComamndArguments(String[] vdmFiles) {
		String[] arguments = new String[vdmFiles.length + 1];
		arguments[0] = VDMTOOLS_POG_FLAG;
		for(int i = 0; i < vdmFiles.length; i++)
			arguments[i + 1] = vdmFiles[i];
		return arguments;
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
