package org.overture.codegen.vdm2cpp;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;



public class CppCodeGenMain 
{
	
	public static void main(String[] args)
	{
		Settings.release = Release.VDM_10;
		Dialect dialect = Dialect.VDM_RT;
		
		boolean gen_timing = false;

		if (args.length <= 1)
		{
			Logger.getLog().println("Wrong input!");
		}

		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);
		
		CppSettings cppSettings = new CppSettings(); 
		
		
		List<File> nfiles = new LinkedList<File>();
		List<String> cg_ignore = new LinkedList<String>();
		//cg_ignore.add("FileReader.vdmrt");
		String setting = args[0];
		String cpp_gen_type = args[1];
		
		if(args.length > 2)
		{
			if(Boolean.parseBoolean(args[2]))
			{
				gen_timing = true;
			}
		}
		File inputRoot = new File(args[3]);
		if (setting.toLowerCase().equals("oo"))
		{
			try
			{
				
				
				List<File> files = GeneralUtils.getFilesRecursive(inputRoot);
				for( File f : files)
				{
					
					if(f.getName().endsWith(".vdmpp") || f.getName().endsWith(".vdmrt"))
					{
						
						nfiles.add(f);
					}
				}

				for(File f2 : nfiles)
				{
					System.out.println(f2.getName());
					
				}

				List<File> libFiles = GeneralUtils.getFiles(new File("src/test/resources/lib".replace('/', File.separatorChar)));
				nfiles.addAll(libFiles);

				GeneratedData data = CppCodeGenUtil.generateCppFromFiles(nfiles, irSettings, cppSettings, dialect,cpp_gen_type,gen_timing);
				List<GeneratedModule> generatedClasses = data.getClasses();

				for (GeneratedModule generatedClass : generatedClasses)
				{
					//Logger.getLog().println("**********");

					if (generatedClass.hasMergeErrors())
					{
						Logger.getLog().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

						CppCodeGenUtil.printMergeErrors(generatedClass.getMergeErrors());
					} else if (!generatedClass.canBeGenerated())
					{
						Logger.getLog().println("Could not generate class: "
								+ generatedClass.getName() + "\n");
						CppCodeGenUtil.printUnsupportedNodes(generatedClass.getUnsupportedInIr());
					} else
					{
						//Logger.getLog().println(generatedClass.getContent());
						File output = new File("target/test-results/"+inputRoot.getName());
						
						if(args.length>3)
							output = new File(args[4]);
						
						CppCodeGenUtil.saveCppClass(output, generatedClass.getName()+".hpp", generatedClass.getContent());
					}

					Logger.getLog().println("\n");
				}

				List<GeneratedModule> quotes = data.getQuoteValues();

				if (quotes != null && !quotes.isEmpty())
				{
					for(GeneratedModule q : quotes)
					{
						//Logger.getLog().println("**********");
						Logger.getLog().println(q.getContent());
					}
				}

				InvalidNamesResult invalidName = data.getInvalidNamesResult();

				if (!invalidName.isEmpty())
				{
					Logger.getLog().println(CppCodeGenUtil.constructNameViolationsString(invalidName));
				}

			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());

			} catch (UnsupportedModelingException e)
			{
				Logger.getLog().println("Could not generate model: "
						+ e.getMessage());
				Logger.getLog().println(CppCodeGenUtil.constructUnsupportedModelingString(e));
			}
		} else if (setting.toLowerCase().equals("exp"))
		{
			try
			{
				Generated generated = CppCodeGenUtil.generateCppFromExp(args[1], irSettings, cppSettings);

				if (generated.hasMergeErrors())
				{
					Logger.getLog().println(String.format("VDM expression '%s' could not be merged. Following merge errors were found:", args[1]));
					CppCodeGenUtil.printMergeErrors(generated.getMergeErrors());
				} else if (!generated.canBeGenerated())
				{
					Logger.getLog().println("Could not generate VDM expression: "
							+ args[1]);
					CppCodeGenUtil.printUnsupportedNodes(generated.getUnsupportedInIr());
				} else
				{
					Logger.getLog().println(generated.getContent().trim());
				}

			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());
			}
		}
	}
}
