package org.overture.codegen.vdm2java;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Settings;

public class JavaCodeGenMain
{
	public static void main(String[] args)
	{
		
		//TODO: Set release to VDM_10:
		//Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_RT;
		
		if (args.length <= 1)
			System.out.println("Wrong input!");
		
		String setting = args[0];
		if(setting.toLowerCase().equals("oo"))
		{
			try
			{
				GeneratedData data = JavaCodeGenUtil.generateJavaFromFiles(args);
				List<GeneratedModule> generatedClasses = data.getClasses();
				
				for (GeneratedModule generatedClass : generatedClasses)
				{
					System.out.println("**********");
					System.out.println(generatedClass.getContent());
				}
				
				GeneratedModule quotes = data.getQuoteValues();
				
				if(quotes != null)
				{
					System.out.println("**********");
					System.out.println(quotes.getContent());
				}
				
				JavaCodeGenUtil.generateJavaSourceFiles(generatedClasses);
				
				JavaCodeGenUtil.generateJavaCodeGenUtils();
				
			} catch (AnalysisException e)
			{
				System.out.println(e.getMessage());
			}
		}
		else if(setting.toLowerCase().equals("exp"))
		{
			try
			{
				String generated = JavaCodeGenUtil.generateJavaFromExp(args[1]);
				System.out.println(generated);
			} catch (AnalysisException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}