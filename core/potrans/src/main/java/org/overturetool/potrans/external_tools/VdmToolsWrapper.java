/**
 * 
 */
package org.overturetool.potrans.external_tools;


/**
 * @author miguel_ferreira
 * 
 */
public class VdmToolsWrapper {
	
	private static final String VDMTOOLS_POG_FLAG = "-g";
	private static final String NO_VDM_FILES_SUPPLIED = "No VDM-PP files supplied.";
	private static final String NO_VDMTOOLS_EXECUTABLE_SUPPLIED = "No VDMTools command line executable supplied.";

	public static String generatePogFile(String vppdeExecutable,
			String[] vdmFiles) throws InputException, ConsoleException {
		validateGeneratePogFileArguments(vppdeExecutable, vdmFiles);
		String[] arguments = buildFlaggedPogComamndArguments(vdmFiles);
		return ConsoleTools.executeProcess(vppdeExecutable, arguments);
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
	 * @throws InputException
	 */
	private static void validateGeneratePogFileArguments(
			String vppdeExecutable, String[] vdmFiles)
			throws InputException {
		validateVdmFilesArgument(vdmFiles);
		validateVppdeExectuableArgument(vppdeExecutable);
	}

	/**
	 * @param vppdeExecutable
	 * @throws InputException
	 */
	private static void validateVppdeExectuableArgument(String vppdeExecutable)
			throws InputException {
		InputValidator.validateStringNotEmptyNorNull(vppdeExecutable, 
				NO_VDMTOOLS_EXECUTABLE_SUPPLIED);
		InputValidator.validateFileExists(vppdeExecutable,
				NO_VDMTOOLS_EXECUTABLE_SUPPLIED);
	}

	/**
	 * @param vdmFiles
	 * @throws InputException
	 */
	private static void validateVdmFilesArgument(String[] vdmFiles)
			throws InputException {
		InputValidator.validateAtLeastOneString(vdmFiles, NO_VDM_FILES_SUPPLIED);
	}
}
