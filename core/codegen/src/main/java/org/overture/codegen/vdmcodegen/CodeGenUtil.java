package org.overture.codegen.vdmcodegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class CodeGenUtil
{
	public static List<GeneratedModule> generateOO(File file) throws AnalysisException
	{
		return generateOO(file, new CodeGen());
	}
	
	public static List<GeneratedModule> generateOO(File file, CodeGen vdmCodGen) throws AnalysisException
	{
		TypeCheckResult<List<SClassDefinition>> typeCheckResult = validateFile(file);

		try
		{
			return vdmCodGen.generateCode(typeCheckResult.result);
		} catch (AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from specification. Exception message: "
					+ e.getMessage());
		}
	}

	public static GeneratedData generateOoFromFile(File file) throws AnalysisException
	{
		return generateOoFromFiles(new String[]{"", file.getAbsolutePath()});
	}
	
	public static GeneratedData generateOoFromFiles(String[] args) throws AnalysisException
	{		
		CodeGen vvdmCodGen = new CodeGen();
		List<GeneratedModule> data = new ArrayList<GeneratedModule>();
		
		for (int i = 1; i < args.length; i++)
		{
			String fileName = args[i];
			File file = new File(fileName);
			data.addAll(generateOO(file, vvdmCodGen));
		}
		
		GeneratedModule quoteValues = vvdmCodGen.generateQuotes();
		
		GeneratedData x = new GeneratedData(data, quoteValues);
		
		return x;
	}

	public static String generateFromExp(String exp) throws AnalysisException
	{
		TypeCheckResult<PExp> typeCheckResult = validateExp(exp);
		
		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp);
		}

		CodeGen vdmCodGen = new CodeGen();
		try
		{
			return vdmCodGen.generateCode(typeCheckResult.result);

		} catch (AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from expression: "
					+ exp + ". Exception message: " + e.getMessage());
		}

	}
	
	public static void generateSourceFiles(List<GeneratedModule> classes)
	{
		CodeGen vdmCodGen = new CodeGen();
		vdmCodGen.generateSourceFiles(classes);
	}
	
	public static void generateCodeGenUtils()
	{
		CodeGen vdmCodGen = new CodeGen();
		vdmCodGen.generateCodeGenUtils();
	}
	
	private static TypeCheckResult<List<SClassDefinition>> validateFile(File file) throws AnalysisException
	{
		if (!file.exists() || !file.isFile())
		{
			throw new AnalysisException("Could not find file: "
					+ file.getAbsolutePath());
		}

		ParserResult<List<SClassDefinition>> parseResult = ParserUtil.parseOo(file);

		if (parseResult.errors.size() > 0)
		{
			throw new AnalysisException("File did not parse: "
					+ file.getAbsolutePath());
		}

		TypeCheckResult<List<SClassDefinition>> typeCheckResult = TypeCheckerUtil.typeCheckPp(file);

		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("File did not pass the type check: "
					+ file.getName());
		}
		
		return typeCheckResult;

	}
	
	
	private static TypeCheckResult<PExp> validateExp(String exp) throws AnalysisException
	{
		if (exp == null || exp.isEmpty())
		{
			throw new AnalysisException("No expression to generate from");
		}

		ParserResult<PExp> parseResult = null;
		try
		{
			parseResult = ParserUtil.parseExpression(exp);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to parse expression: " + exp
					+ ". Message: " + e.getMessage());
		}

		if (parseResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to parse expression: " + exp);
		}

		TypeCheckResult<PExp> typeCheckResult = null;
		try
		{
			typeCheckResult = TypeCheckerUtil.typeCheckExpression(exp);
		} catch (Exception e)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp + ". Message: " + e.getMessage());
		}
		
		return typeCheckResult;
	}
	
}
