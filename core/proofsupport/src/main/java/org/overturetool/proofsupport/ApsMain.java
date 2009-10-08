package org.overturetool.proofsupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsPoProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;

public class ApsMain {

	@Option(name = "-t", usage = "run APS in translate mode")
	private boolean translation;

	@Option(name = "-vppde", usage = "path to the vppde binary file", metaVar = "VPPDE_BINARY", required = true)
	private String vppdeBinary = "vppde";

	@Option(name = "-p", usage = "run APS in proof mode")
	private boolean proof;

	@Option(name = "-mosml", usage = "path to the mosml directory", metaVar = "MOSML_DIR")
	private String mosmlDir = "";

	@Option(name = "-hol", usage = "path to the hol directory", metaVar = "HOL_DIR")
	private String holDir = "";

	@Argument
	private List<String> arguments = new ArrayList<String>();

	private String vdmModelFile = "";
	private List<String> vdmContextFiles = new ArrayList<String>(0);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ApsMain apsMain = new ApsMain();
			apsMain.parseArguments(args);
			apsMain.run();
		} catch (CmdLineException e) {
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run() throws AutomaicProofSystemException, IOException {
		AutomaticProofSystemBatch aps = new AutomaticProofSystemBatch(mosmlDir, holDir,
				new VdmToolsWrapper(vppdeBinary), new VdmToolsPoProcessor());
		if (translation)
			System.out.println(aps.translateModelAndPos(vdmModelFile,
					vdmContextFiles));
		else if (proof)
			for (String outputLine : aps.dischargeAllPos(vdmModelFile,
					vdmContextFiles))
				System.out.println(outputLine);
	}

	private void parseArgumentModelFiles() {
		vdmModelFile = arguments.get(0);
		if (arguments.size() > 1)
			vdmContextFiles = arguments.subList(1, arguments.size() - 1);
	}

	public void parseArguments(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);

		// if you have a wider console, you could increase the value;
		// here 80 is also the default
		parser.setUsageWidth(80);

		try {
			// parse the arguments.
			parser.parseArgument(args);

			validateArguments();

			parseArgumentModelFiles();

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err
					.println("java ApsMain [-t|[-p -hol HOL_DIR -mosml MOSML_DIR]] -vppde VPPDE_BINARY <vpp files>");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();

			// print option sample. This is useful some time
			System.err
					.println("  Example: java ApsMain -t -vppde \"/path_to_vdmtools/bin/vppde\" model.vpp");
			System.err
					.println("  Example: java ApsMain -p -hol \"/path_to_hol\" -mosml \"/path_to_mosml\" -vppde \"/path_to_vdmtools/bin/vppde\" model.vpp");

			throw new CmdLineException("");
		}
	}

	public void validateArguments() throws CmdLineException {
		if (!proof && !translation)
			throw new CmdLineException("Must chose one of the operation modes.");

		if (proof && translation)
			throw new CmdLineException("Can't choose both operation modes.");

		if (proof && (mosmlDir.equals("") && holDir.equals("")))
			throw new CmdLineException(
					"To operate on proof mode you need to supply -mosml and -hol arguments.");

		if (arguments.isEmpty())
			throw new CmdLineException("No argument is given");
	}

}
