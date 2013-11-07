package org.overture.codegen.vdmcodegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class JavaCodeGenUtil
{
	public static List<GeneratedModule> generateJava(File file) throws AnalysisException
	{
		return generateJava(file, new CodeGen());
	}
	
	public static List<GeneratedModule> generateJava(File file, CodeGen vdmCodGen) throws AnalysisException
	{
		TypeCheckResult<List<SClassDefinition>> typeCheckResult = GeneralCodeGenUtils.validateFile(file);

		try
		{
			return vdmCodGen.generateCode(typeCheckResult.result);
		} catch (AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from specification. Exception message: "
					+ e.getMessage());
		}
	}

	public static GeneratedData generateJavaFromFile(File file) throws AnalysisException
	{
		return generateJavaFromFiles(new String[]{"", file.getAbsolutePath()});
	}
	
	public static GeneratedData generateJavaFromFiles(String[] args) throws AnalysisException
	{		
		CodeGen vvdmCodGen = new CodeGen();
		List<GeneratedModule> data = new ArrayList<GeneratedModule>();
		
		for (int i = 1; i < args.length; i++)
		{
			String fileName = args[i];
			File file = new File(fileName);
			data.addAll(generateJava(file, vvdmCodGen));
		}
		
		GeneratedModule quoteValues = vvdmCodGen.generateQuotes();
		
		GeneratedData x = new GeneratedData(data, quoteValues);
		
		return x;
	}

	public static String generateJavaFromExp(String exp) throws AnalysisException
	{
		TypeCheckResult<PExp> typeCheckResult = GeneralCodeGenUtils.validateExp(exp);
		
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
	
	public static void generateJavaSourceFiles(List<GeneratedModule> classes)
	{
		CodeGen vdmCodGen = new CodeGen();
		vdmCodGen.generateSourceFiles(classes);
	}
	
	public static void generateJavaCodeGenUtils()
	{
		CodeGen vdmCodGen = new CodeGen();
		vdmCodGen.generateCodeGenUtils();
	}
	
}
