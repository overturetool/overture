package org.overture.codegen.vdm2jml;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGenMain;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class JmlGenMain
{
	public static final String OUTPUT_ARG = "-output";
	public static final String PRINT_ARG = "-print";
	public static final String FOLDER_ARG = "-folder";
	public static final String INVARIANT_FOR = "-invariant_for";

	public static void main(String[] args)
	{
		if (args == null || args.length < 1)
		{
			usage("Expected one or more arguments");
			return;
		}

		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_SL;

		List<String> listArgs = Arrays.asList(args);

		List<File> files = new LinkedList<File>();
		
		File outputDir = null;

		boolean print = false;
		
		JmlGenerator jmlGen = new JmlGenerator();
		jmlGen.getIrSettings().setCharSeqAsString(true);

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
			else if(arg.equals(INVARIANT_FOR))
			{
				jmlGen.getJmlSettings().setGenInvariantFor(true);
			}
			else
			{
				// It's a file or a directory
				File file = new File(arg);

				if (file.isFile())
				{
					if (JavaCodeGenUtil.isSupportedVdmSourceFile(file))
					{
						files.add(file);
					}
				} else
				{
					usage("Not a file: " + file);
				}
			}
		}

		//GeneralUtils.deleteFolderContents(outputDir, true);

		try
		{
			Logger.getLog().println("Starting the VDM to JML generator...");
			
			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);
			
			if(!GeneralCodeGenUtils.hasErrors(tcResult))
			{
				GeneratedData data = jmlGen.generateJml(tcResult.result);
				JavaCodeGenMain.processData(print, outputDir, jmlGen.getJavaGen(), data);
			}
			else
			{
				Logger.getLog().printErrorln("Could not parse/type check VDM model:\n"
						+ GeneralCodeGenUtils.errorStr(tcResult));
			}
			

		} catch (AnalysisException e)
		{
			Logger.getLog().println("Could not code generate model: "
					+ e.getMessage());
		}
	}

	private static void usage(String msg)
	{
		Logger.getLog().printErrorln("VDMSL to JML/Java generator: " + msg + "\n");
		Logger.getLog().printErrorln("Usage: vdm2jml [<options>] [<VDM SL files>]");
		Logger.getLog().printErrorln(PRINT_ARG + ": print the generated code to the console");
		Logger.getLog().printErrorln(OUTPUT_ARG + " <folder path>: the output folder of the generated code");
		Logger.getLog().printErrorln(FOLDER_ARG + " <folder path>: a folder containing input .vdmsl files");
		Logger.getLog().printErrorln(INVARIANT_FOR
				+ ": to check record invariants explicitly using JML's invariant_for");
		System.exit(1);
	}
}
