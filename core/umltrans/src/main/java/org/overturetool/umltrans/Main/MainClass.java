package org.overturetool.umltrans.Main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class MainClass {

	@Option(name = "-uml", usage = "Convert to uml")
	private boolean convertToUml;

	@Option(name = "-vpp", usage = "Convert to VDM")
	private boolean convertToVdm;

	@Option(name = "-r", usage = "Remove tex tags")
	private boolean removeTex;

	@Option(name = "-x", usage = "Print XML parse result")
	private boolean printXml;

	@Option(name = "-output", usage = "Path to output file/dir", metaVar = "c:\\tmp", required = true)
	private String outputLocation = "";
	private File outputLocationFile = null;

	@Argument
	private List<String> arguments = new ArrayList<String>();

	private List<File> contextFiles = new ArrayList<File>(0);

	private void parseArgumentModelFiles() {
		// vdmModelFile = arguments.get(0);
		if (arguments.size() > 0)
			for (String argument : arguments)
				contextFiles.add(new File(argument));
	}

	public void parseArguments(String[] args) throws CmdLineException {
		CmdLineParser parser = new CmdLineParser(this);

		// if you have a wider console, you could increase the value;
		// here 80 is also the default
		parser.setUsageWidth(80);
		
		try {
			// parse the arguments.
			parser.parseArgument(args);

			parseArgumentModelFiles();

			validateArguments();

		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
//			System.err
//					.println("java -jar myprogram.jar [options...] arguments...");
//			parser.printUsage(System.err);
			
			parser.printSingleLineUsage(System.err);
			System.err.print(" context files");
			System.err.println();
			parser.printUsage(System.err);
			
			throw new CmdLineException("Error in: "+ args);
		}
	}

	public void validateArguments() throws CmdLineException {

		if (arguments.isEmpty())
			throw new CmdLineException("No argument is given");

		if (contextFiles.isEmpty())
			throw new CmdLineException("No files given for transformation");

		for (File file : contextFiles)
			if (!file.exists())
				throw new CmdLineException("File: " + file.getName()
						+ " does not exist");

		outputLocationFile = new File(outputLocation);

		if ((removeTex || printXml || convertToVdm)
				&& !outputLocationFile.isDirectory())
			throw new CmdLineException("Output must be a folder");

		if (convertToUml && outputLocationFile!=null && outputLocationFile.getName().length()<1)
			throw new CmdLineException("Output must be a file");

	}

	private void run() throws Exception {
		Long beginTime = System.currentTimeMillis();
		if (removeTex)
			CmdLineProcesser.removeTex(outputLocationFile, contextFiles);
		if (convertToUml)
			CmdLineProcesser.toUml(outputLocationFile, contextFiles);
		if (convertToVdm)
			CmdLineProcesser.toVpp(outputLocationFile, contextFiles);
		if (printXml)
			CmdLineProcesser.printXmlDoc(contextFiles.get(0),
					outputLocationFile);

		System.out.println("Command completed in "
				+ (double) (System.currentTimeMillis() - beginTime) / 1000
				+ " secs");
	}

	/**
	 * @param args
	 * @throws Exception 
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		try {

			MainClass apsMain = new MainClass();
			apsMain.parseArguments(args);
			apsMain.run();

		} catch (CmdLineException e) {throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

}
