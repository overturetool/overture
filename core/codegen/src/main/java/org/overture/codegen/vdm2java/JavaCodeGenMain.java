package org.overture.codegen.vdm2java;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.InvalidNamesException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;

public class JavaCodeGenMain
{
	private static void printUnsupportedNodes(Set<INode> unsupportedNodes)
	{
		List<INode> nodesSorted = LocationAssistantCG.getNodeLocationsSorted(unsupportedNodes);
		
		Logger.getLog().println("Following constructs are not supported: ");
		
		for (INode node : nodesSorted)
		{
			Logger.getLog().print(node.toString());
			
			ILexLocation location = LocationAssistantCG.findLocation(node);
			
			Logger.getLog().println(location != null ? " at [line, pos] = [" + location.getStartLine() + ", " + location.getStartPos() + "]": "");
			
		}
	}
	
	public static void main(String[] args)
	{
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_RT;
		
		if (args.length <= 1)
			Logger.getLog().println("Wrong input!");
		
		String setting = args[0];
		if(setting.toLowerCase().equals("oo"))
		{
			try
			{
				List<File> files = GeneralUtils.getFilesFromPaths(args);
				
				List<File> libFiles = GeneralUtils.getFiles(new File("src\\test\\resources", IOoAstConstants.UTIL_RESOURCE_FOLDER));
				files.addAll(libFiles);
				
				GeneratedData data = JavaCodeGenUtil.generateJavaFromFiles(files);
				List<GeneratedModule> generatedClasses = data.getClasses();
				
				for (GeneratedModule generatedClass : generatedClasses)
				{
					Logger.getLog().println("**********");
					
					if(generatedClass.canBeGenerated())
					{
						Logger.getLog().println(generatedClass.getContent());
					}
					else
					{
						Logger.getLog().println("Could not generate class: " + generatedClass.getName() + "\n");
						printUnsupportedNodes(generatedClass.getUnsupportedNodes());
					}
					
					Logger.getLog().println("\n");
				}
				
				GeneratedModule quotes = data.getQuoteValues();
				
				if(quotes != null)
				{
					Logger.getLog().println("**********");
					Logger.getLog().println(quotes.getContent());
				}

				File file = new File("target" + IText.SEPARATOR_CHAR + "sources"
						+ IText.SEPARATOR_CHAR);

				JavaCodeGenUtil.generateJavaSourceFiles(file, generatedClasses);
				
				List<GeneratedModule> utils = JavaCodeGenUtil.generateJavaCodeGenUtils();
				JavaCodeGenUtil.generateJavaSourceFiles(file, utils);
				
			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());

			} catch (InvalidNamesException e)
			{
				Logger.getLog().println("Could not generate model: "
						+ e.getMessage());
				Logger.getLog().println(JavaCodeGenUtil.constructNameViolationsString(e));
			} catch (UnsupportedModelingException e)
			{
				Logger.getLog().println("Could not generate model: "
						+ e.getMessage());
				Logger.getLog().println(JavaCodeGenUtil.constructUnsupportedModelingString(e));
			} catch (IOException e)
			{
				Logger.getLog().println("Could not generate utils: "
						+ e.getMessage());
			}
		}
		else if(setting.toLowerCase().equals("exp"))
		{
			try
			{
				Generated generated = JavaCodeGenUtil.generateJavaFromExp(args[1]);
				
				if(generated.canBeGenerated())
					Logger.getLog().println(generated.getContent());
				else
				{
					Logger.getLog().println("Could not generate VDM expression: " + args[1]);
					printUnsupportedNodes(generated.getUnsupportedNodes());
				}
				
			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());
			}
		}
	}
}