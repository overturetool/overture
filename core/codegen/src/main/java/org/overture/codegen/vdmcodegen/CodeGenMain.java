package org.overture.codegen.vdmcodegen;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;

public class CodeGenMain
{
	public static void main(String[] args)
	{
		Settings.dialect = Dialect.VDM_RT;
		
		if (args.length <= 1)
			System.out.println("Wrong input!");
		
		String setting = args[0];
		if(setting.toLowerCase().equals("oo"))
		{
			try
			{
				List<String> generatedClasses = CodeGenUtil.generateOO(args, true);
				
				for (String generatedClass : generatedClasses)
				{
					System.out.println("**********");
					System.out.println(generatedClass);
				}
				
				CodeGenUtil.generateCodeGenUtils();
				
			} catch (AnalysisException e)
			{
				System.out.println(e.getMessage());
			}
		}
		else if(setting.toLowerCase().equals("exp"))
		{
			try
			{
				String generated = CodeGenUtil.generateFromExp(args[1]);
				System.out.println(generated);
			} catch (AnalysisException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}