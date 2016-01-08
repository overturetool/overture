package org.overture.codegen.cgen;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class CGenMain
{

	private static final String FOLDER_ARG = "-folder";
	private static final String PRINT_ARG = "-print";
	public static final String OUTPUT_ARG = "-output";
	private static String output_folder = null;

	public static void main(String[] args)
	{

		if (args == null || args.length < 1)
		{
			usage("Expected one or more arguments");
			return;
		}

		List<String> listArgs = Arrays.asList(args);
		List<File> files = new LinkedList<File>();
		File outputDir = null;
		boolean print = false;

		// File file = new File(args[0]);

		// List<File> files = new LinkedList<>();
		// files.add(file);

		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			if (arg.equals(OUTPUT_ARG))
			{
				if (i.hasNext())
				{
					outputDir = new File(i.next());
					outputDir.mkdirs();
					output_folder = outputDir.toString();

					if (!outputDir.isDirectory())
					{
						usage(outputDir + " is not a directory");
					}

				} else
				{
					usage(OUTPUT_ARG + " requires a directory");
				}
			} else if (arg.equals(PRINT_ARG))
			{
				print = true;
			} else if (arg.equals(FOLDER_ARG))
			{
				if (i.hasNext())
				{
					File path = new File(i.next());

					if (path.isDirectory())
					{
						files.addAll(filterFiles(GeneralUtils.getFiles(path)));
					} else
					{
						usage("Could not find path: " + path);
					}
				} else
				{
					usage(FOLDER_ARG + " requires a directory");
				}
			} else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (isRtFile(file))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}

		try
		{
			List<SClassDefinition> ast = GeneralCodeGenUtils.consClassList(files, Dialect.VDM_RT);

			CGen cGen = new CGen();

			GeneratedData data = cGen.generateCFromVdm(ast, output_folder);

			System.out.println("Generation complete");
			if (print)
			{
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

	private static void usage(String msg)
	{
		Logger.getLog().printErrorln("VDM-RT to C generator: " + msg + "\n");
		Logger.getLog().printErrorln("Usage: vdm2c [<options>] [<VDM-RT files>]");
		Logger.getLog().printErrorln(PRINT_ARG
				+ ": print the generated code to the console");
		Logger.getLog().printErrorln(OUTPUT_ARG
				+ " <folder path>: the output folder of the generated code");
		Logger.getLog().printErrorln(FOLDER_ARG
				+ " <folder path>: a folder containing input .vdmrt files");

		System.exit(1);
	}
}
