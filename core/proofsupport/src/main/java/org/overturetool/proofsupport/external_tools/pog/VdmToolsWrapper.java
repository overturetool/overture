package org.overturetool.proofsupport.external_tools.pog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Console;

public class VdmToolsWrapper implements PogGenerator {

	private static final String POG_FILE_EXTENSION = ".pog";
	protected static final String VDMTOOLS_POG_FLAG = "-g";
	protected final String vppdeBinaryPath;

	public VdmToolsWrapper(String vppdeBinaryPath) throws IOException {
		this.vppdeBinaryPath = vppdeBinaryPath;
	}

	public String generatePogFile(String[] vdmFiles) throws PogGeneratorException {
		if ((vdmFiles != null) && (vdmFiles.length > 0)) {
			return runPogGenerator(vdmFiles);
		}
		else
			throw new PogGeneratorException("No VDM files supplied.");
	}

	private String runPogGenerator(String[] vdmFiles) throws PogGeneratorException {
		List<String> command = buildPogCommand(vppdeBinaryPath, vdmFiles);
		Console vdmToolsConsole = createVdmToolsConsole(command);
		waitForPogToFinish(vdmToolsConsole);
		return vdmFiles[0] + POG_FILE_EXTENSION;
	}

	private void waitForPogToFinish(Console vdmToolsConsole) throws PogGeneratorException {
		try {
			vdmToolsConsole.waitFor();
		} catch (InterruptedException e) {
			throw new PogGeneratorException("Interrupted while generating proof obligations.", e);
		}
	}

	private Console createVdmToolsConsole(List<String> command) throws PogGeneratorException {
		Console vdmConsole;
		try {
			vdmConsole = new Console(command);
		} catch (IOException e) {
			throw new PogGeneratorException("IO error while connecting to the VDMTools CLI.", e);
		}
		return vdmConsole;
	}

	protected static List<String> buildPogCommand(String vppdeCommand,
			String[] vdmFiles) {
		ArrayList<String> command = new ArrayList<String>(vdmFiles.length + 2);
		command.add(vppdeCommand);
		command.add(VDMTOOLS_POG_FLAG);
		for (String file : vdmFiles)
			command.add(file);
		return command;
	}

}
