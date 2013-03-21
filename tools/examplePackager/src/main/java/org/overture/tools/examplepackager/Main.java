/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.examplepackager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.overture.ast.lex.Dialect;

public class Main
{
	static File input;
	static File output = new File(".");
	static boolean zip = false;
	static boolean web = false;

	/**
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		// create the command line parser
		CommandLineParser parser = new PosixParser();

		// create the Options
		Options options = new Options();
		Option helpOpt = new Option("?", "help", false, "print this message");

		Option genWebOpt = new Option("w","web", false, "generate website");
		Option genZipbundleOpt = new Option("z","zip", false, "generate zip bundles");

		Option inputOpt = new Option("i", "input", true, "the path of the examples folder");
		inputOpt.setRequired(true);
		Option outputOpt = new Option("o", "output", true, "the path to where output files are written");

		options.addOption(helpOpt);
		options.addOption(genWebOpt);
		options.addOption(genZipbundleOpt);
		options.addOption(inputOpt);
		options.addOption(outputOpt);

		CommandLine line = null;
		try
		{
			// parse the command line arguments
			line = parser.parse(options, args);

			if (line.hasOption(helpOpt.getOpt()))
			{
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("examplepackager", options);
				return ;
			}

		} catch (ParseException exp)
		{
			System.err.println("Unexpected exception:" + exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("examplepackager", options);
			return ;
		}

		input = new File(line.getOptionValue(inputOpt.getOpt()));
		if (line.hasOption(outputOpt.getOpt()))
		{
			output = new File(line.getOptionValue(outputOpt.getOpt()));
		}

		web = line.hasOption(genWebOpt.getOpt());
		zip = line.hasOption(genZipbundleOpt.getOpt());

		runCompleteTest(new File(args[1]));
		return ;
	}

	private static Controller runController(Dialect dialect,
			File inputRootFolder, File tmpFolder) throws IOException
	{
		Controller controller = new Controller(dialect, inputRootFolder, output);

		if (zip)
		{
			output.mkdirs();

			File zipFile = new File(output, "Examples"
					+ dialect.toString().toUpperCase() + ".zip");
			controller.packExamples(tmpFolder, zipFile,false);

			zipFiles.add(zipFile);
		}
		if (web)
		{
			controller.packExamples(tmpFolder, null,true);
			controller.createWebSite();
		}
		return controller;
	}

	static List<File> zipFiles = new Vector<File>();

	public static void runCompleteTest(File root) throws Exception
	{
		if (!root.getName().toLowerCase().equals("examples"))
			throw new Exception("Illegal root");

		File tmpFolder = new File("tmp");

		List<Controller> controllers = new Vector<Controller>();

		Controller controller = runController(Dialect.VDM_SL, new File(root, "VDMSL"), tmpFolder);
		controllers.add(controller);
		Controller.delete(tmpFolder);

		controller = runController(Dialect.VDM_PP, new File(root, "VDM++"), tmpFolder);
		controllers.add(controller);
		Controller.delete(tmpFolder);

		controller = runController(Dialect.VDM_RT, new File(root, "VDMRT"), tmpFolder);
		controllers.add(controller);
		
		if (web)
		{
			controller.createWebOverviewPage(controllers, zipFiles);
		}
		Controller.delete(tmpFolder);

		System.out.println("Done.");

		System.exit(0);

	}
}
