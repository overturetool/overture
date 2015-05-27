package org.overture.codegen.vdm2jml;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.util.modules.ModuleList;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGenMain;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Release;
import org.overture.config.Settings;

public class JmlGenMain
{
	public static final String OUTPUT_ARG = "-output";
	public static final String PRINT_ARG = "-print";
	public static final String REPORT_VIOLATIONS_ARG = "-report";
	public static final String FOLDER_ARG = "-folder";

	public static void main(String[] args)
	{
		if (args == null || args.length < 1)
		{
			usage("Expected one or more arguments");
			return;
		}

		Settings.release = Release.VDM_10;
		// Dialect dialect = Dialect.VDM_SL;

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(true);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(true);
		irSettings.setGeneratePostCondChecks(false);
		irSettings.setGenerateInvariants(true);

		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);
		//javaSettings.setJavaRootPackage("my.pack");
		javaSettings.setGenRecsAsInnerClasses(false);

		List<String> listArgs = Arrays.asList(args);

		List<File> files = new LinkedList<File>();

		File outputDir = null;

		boolean print = false;
		boolean report = false;

		for (Iterator<String> i = listArgs.iterator(); i.hasNext();)
		{
			String arg = i.next();

			if (arg.equals(OUTPUT_ARG))
			{
				if (i.hasNext())
				{
					outputDir = new File(i.next());
					outputDir.mkdirs();

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
			} 
			else if (arg.equals(FOLDER_ARG))
			{
				if (i.hasNext())
				{
					File path = new File(i.next());

					if (path.isDirectory())
					{
						files.addAll(JavaCodeGenMain.filterFiles(GeneralUtils.getFiles(path)));
					} else
					{
						usage("Could not find path: " + path);
					}
				} else
				{
					usage(FOLDER_ARG + " requires a directory");
				}
			}
			else if(arg.equals(REPORT_VIOLATIONS_ARG))
			{
				report = true;
			}
			else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (JavaCodeGenMain.isValidSourceFile(file))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}

		JmlGenerator jmlGen = new JmlGenerator();

		jmlGen.setIrSettings(irSettings);
		jmlGen.setJavaSettings(javaSettings);
		
		jmlGen.getJmlSettings().setInjectReportCalls(report);

		//GeneralUtils.deleteFolderContents(outputDir, true);

		try
		{
			Logger.getLog().println("Starting the VDM to JML generator...");
			ModuleList modules = GeneralCodeGenUtils.consModuleList(files);

			GeneratedData data = jmlGen.generateJml(modules);

			JavaCodeGenMain.processData(print, outputDir, jmlGen.getJavaGen(), data);

		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: "
					+ e.getMessage());
		} catch (UnsupportedModelingException e)
		{
			Logger.getLog().println("Could not generate model: "
					+ e.getMessage());
			Logger.getLog().println(GeneralCodeGenUtils.constructUnsupportedModelingString(e));
		}
	}

	private static void usage(String msg)
	{
		Logger.getLog().printErrorln("VDMSL to JML/Java generator: " + msg
				+ "\n");
		Logger.getLog().printErrorln("Usage: vdm2jml [<options>] [<VDM SL files>]");
		Logger.getLog().printErrorln(PRINT_ARG
				+ ": print the generated code to the console");
		Logger.getLog().printErrorln(OUTPUT_ARG
				+ " <folder path>: the output folder of the generated code");
	}
}
