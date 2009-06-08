package org.overturetool.potrans.external_tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VdmToolsConsole extends Console {

	protected static final String VDMTOOLS_POG_FLAG = "-g";

	public VdmToolsConsole(List<String> command) throws IOException {
		super(command);
	}

	public static String generatePogFile(String vppdeCommand,
			String[] vdmFiles) throws IOException, InterruptedException {
		String result = null;
		
		List<String> command = buildPogCommand(vppdeCommand, vdmFiles);
		VdmToolsConsole vdmConsole = new VdmToolsConsole(command);
		int exitValue = vdmConsole.waitFor();
		if(exitValue == 0) {
			result = vdmConsole.readAllLines();
		}
		
		return result;
	}

	protected static List<String> buildPogCommand(String vppdeCommand,
			String[] vdmFiles) {
		ArrayList<String> command = new ArrayList<String>(vdmFiles.length + 2);
		command.add(vppdeCommand);
		command.add(VDMTOOLS_POG_FLAG);
		for(String file : vdmFiles)
			command.add(file);
		return command;
	}

}
