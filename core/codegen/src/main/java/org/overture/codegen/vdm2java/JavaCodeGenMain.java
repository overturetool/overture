package org.overture.codegen.vdm2java;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.utils.InvalidNamesException;
import org.overture.config.Release;
import org.overture.config.Settings;

public class JavaCodeGenMain
{
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
				GeneratedData data = JavaCodeGenUtil.generateJavaFromFiles(args);
				List<GeneratedModule> generatedClasses = data.getClasses();
				
				for (GeneratedModule generatedClass : generatedClasses)
				{
					Logger.getLog().println("**********");
					Logger.getLog().println(generatedClass.getContent());
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
				Logger.getLog().println("Could not generate model: " + e.getMessage());
				Logger.getLog().println(JavaCodeGenUtil.constructNameViolationsString(e));
			} catch (IOException e)
			{
				Logger.getLog().println("Could not generate utils: " + e.getMessage());
			}
		}
		else if(setting.toLowerCase().equals("exp"))
		{
			try
			{
				String generated = JavaCodeGenUtil.generateJavaFromExp(args[1]);
				Logger.getLog().println(generated);
			} catch (AnalysisException e)
			{
				Logger.getLog().println(e.getMessage());
			}
		}
	}
}