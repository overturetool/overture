package org.overture.codegen.cgen;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.apache.commons.cli.*;
import org.overture.codegen.utils.GeneralUtils;

public class CGenMain
{

	public static void main(String[] args)
	{

		// create Options object
		Options options = new Options();

		// add t option
		Option verboseOpt = Option.builder("v").longOpt("verbose").desc("Print processing information").build();
		Option sourceOpt = Option.builder("sf").longOpt("folder").desc("Path to a source folder containing VDM files").hasArg().build();
		Option destOpt = Option.builder("dest").longOpt("destination").desc("Output directory").hasArg().required().build();

		options.addOption(verboseOpt);
		options.addOption(sourceOpt);
		options.addOption(destOpt);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try
		{
			cmd = parser.parse(options, args);
		} catch (ParseException e1)
		{
			System.err.println("Parsing failed.  Reason: " + e1.getMessage());
			return;
		}

		List<File> files = new LinkedList<File>();
		File outputDir = null;
		boolean print = false;

		print = cmd.hasOption(verboseOpt.getOpt());

		if (cmd.hasOption(sourceOpt.getOpt()))
		{
			File path = new File(cmd.getOptionValue(sourceOpt.getOpt()));

			if (!path.isDirectory())
			{
				usage(options, sourceOpt, outputDir + " is not a directory");
				return;
			}

			files.addAll(filterFiles(GeneralUtils.getFiles(path)));

		}

		if (cmd.hasOption(destOpt.getOpt()))
		{
			File outputPath = new File(cmd.getOptionValue(destOpt.getOpt()).replace('/', File.separatorChar));
			outputPath.mkdirs();
			if (!outputPath.isDirectory())
			{
				usage(options, destOpt, outputDir + " is not a directory");
				return;
			}
			outputDir = outputPath;

		}else
		{
			outputDir = new File("target/cgen".replace('/', File.separatorChar));
			
		}
		final String[] remainingArguments = cmd.getArgs();

		for (String s : remainingArguments)
		{
			File f = new File(s);
			if (f.exists() && f.isFile())
			{
				files.add(f);
			}
		}


		try
		{
			List<SClassDefinition> ast = GeneralCodeGenUtils.consClassList(files, Dialect.VDM_RT);

			CGen xGen = new CGen();

			GeneratedData data = xGen.generateXFromVdm(ast, outputDir);

			for (GeneratedModule module : data.getClasses())
			{

				if (module.canBeGenerated())
				{
					System.out.println(module.getContent());
					System.out.println(module.getUnsupportedInIr());
					System.out.println(module.getMergeErrors());
					System.out.println(module.getUnsupportedInTargLang());
				}
			}

		} catch (AnalysisException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<File> filterFiles(List<File> files)
	{
		List<File> filtered = new LinkedList<File>();

		for (File f : files)
		{
			if (isRtFile(f))
			{
				filtered.add(f);
			}
		}

		return filtered;
	}

	private static boolean isRtFile(File f)
	{
		return f.getName().endsWith(".vdmrt") || f.getName().endsWith(".vrt");
	}

	private static void usage(Options options, Option opt, String string)
	{
		System.err.println("Error in argument: " + opt.getOpt() + " - "
				+ string);
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("cgen", options);

	}
}
