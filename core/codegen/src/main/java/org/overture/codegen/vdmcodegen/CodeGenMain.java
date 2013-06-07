package org.overture.codegen.vdmcodegen;

import java.io.File;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class CodeGenMain
{
	public static void main(String[] args)
	{
		Settings.dialect = Dialect.VDM_RT;
		
		if (args.length <= 1)
			System.out.println("Wrong input!");
		
		String setting = args[0];
		
		if(setting.toLowerCase().equals("oo"))
			generateOO(args);
		else if(setting.toLowerCase().equals("exp"))
			generateFromExp(args[1]);

		
	}
	
	public static void generateFromExp(String exp)
	{
		if(exp == null || exp.isEmpty())
		{
			System.out.println("No expression to generate from");
			return;
		}
		
		
		ParserResult<PExp> parseResult = null;
		try
		{
			parseResult = ParserUtil.parseExpression(exp);
		} catch (ParserException | LexException e)
		{
			System.out.println("Unable to parse expression: " + exp + ". Message: " + e.getMessage());
		}

		if (parseResult.errors.size() > 0)
		{
			System.out.println("Unable to parse expression: " + exp);
			return;
		}
		
		TypeCheckResult<PExp> typeCheckResult = null; 
		try
		{
			typeCheckResult = TypeCheckerUtil.typeCheckExpression(exp);
		} catch (ParserException | LexException e)
		{
			System.out.println("Unable to type check expression: " + exp + ". Message: " + e.getMessage());
			return;
		}
		
		
		if(typeCheckResult.errors.size() > 0)
		{
			System.out.println("Unable to type check expression: " + exp);
			return;
		}
		
		VdmCodeGen vdmCodGen = new VdmCodeGen();
		try
		{
			vdmCodGen.generateCode(typeCheckResult.result);
		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void generateOO(String[] args)
	{
		for (int i = 1; i < args.length; i++)
		{
			String fileName = args[i];
			
			File file = new File(fileName);

			if (!file.exists() || !file.isFile())
			{
				System.out.println("Could not find file: " + file.getAbsolutePath());
				continue;
			}

			ParserResult<List<SClassDefinition>> parseResult = ParserUtil.parseOo(file);

			if (parseResult.errors.size() > 0)
			{
				System.out.println("File did not parse: " + file.getAbsolutePath());
				continue;
			}
			
			TypeCheckResult<List<SClassDefinition>> typeCheckResult = TypeCheckerUtil.typeCheckPp(file);
			
			if(typeCheckResult.errors.size() > 0)
			{
				System.out.println("File did not pass the type check: " + fileName);
				continue;
			}
				
			VdmCodeGen vdmCodGen = new VdmCodeGen();
			try
			{
				vdmCodGen.generateCode(typeCheckResult.result);
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
					
		}
	}

}
