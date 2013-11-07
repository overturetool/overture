package org.overture.codegen.vdm2java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;

public class JavaCodeGenUtil
{
	public static List<GeneratedModule> generateJava(File file) throws AnalysisException
	{
		return generateJava(file, new JavaCodeGen());
	}
	
	public static List<GeneratedModule> generateJava(File file, JavaCodeGen vdmCodGen) throws AnalysisException
	{
		TypeCheckResult<List<SClassDefinition>> typeCheckResult = GeneralCodeGenUtils.validateFile(file);

		try
		{
			return vdmCodGen.generateJavaFromVdm(typeCheckResult.result);
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
		JavaCodeGen vvdmCodGen = new JavaCodeGen();
		List<GeneratedModule> data = new ArrayList<GeneratedModule>();
		
		for (int i = 1; i < args.length; i++)
		{
			String fileName = args[i];
			File file = new File(fileName);
			data.addAll(generateJava(file, vvdmCodGen));
		}
		
		GeneratedModule quoteValues = vvdmCodGen.generateJavaFromVdmQuotes();
		
		GeneratedData dataToReturn = new GeneratedData(data, quoteValues);
		
		return dataToReturn;
	}

	public static String generateJavaFromExp(String exp) throws AnalysisException
	{
		TypeCheckResult<PExp> typeCheckResult = GeneralCodeGenUtils.validateExp(exp);
		
		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp);
		}

		JavaCodeGen vdmCodGen = new JavaCodeGen();
		try
		{
			return vdmCodGen.generateJavaFromVdmExp(typeCheckResult.result);

		} catch (AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from expression: "
					+ exp + ". Exception message: " + e.getMessage());
		}

	}
	
	public static void generateJavaSourceFiles(List<GeneratedModule> classes)
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.generateJavaSourceFiles(classes);
	}
	
	public static void generateJavaCodeGenUtils()
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.generateJavaCodeGenUtils();
	}
	
	public static String formatJavaCode(String code)
	{
		StringBuffer b = new StringBuffer();
		try
		{
			File tempFile = new File("temp.java");
			FileWriter xwriter = new FileWriter(tempFile);
			xwriter.write(code.toString());
			xwriter.flush();

			Jalopy jalopy = new Jalopy();
			jalopy.setFileFormat(FileFormat.DEFAULT);
			jalopy.setInput(tempFile);
			jalopy.setOutput(b);
			jalopy.format();

			xwriter.close();
			tempFile.delete();

			if (jalopy.getState() == Jalopy.State.OK
					|| jalopy.getState() == Jalopy.State.PARSED)
				return b.toString();
			else if (jalopy.getState() == Jalopy.State.WARN)
				return null;// formatted with warnings
			else if (jalopy.getState() == Jalopy.State.ERROR)
				return null; // could not be formatted

		} catch (Exception e)
		{
			Logger.getLog().printErrorln("Could not format code: "
					+ e.toString());
			e.printStackTrace();
		}

		return null;// could not be formatted
	}
	
	public static void saveJavaClass(String javaFileName, String code)
	{
		try
		{
			new File("target\\sources\\").mkdirs();
			String file_name = "target\\sources\\" + javaFileName;
			FileWriter file = new FileWriter(file_name);
			BufferedWriter out = new BufferedWriter(file);
			out.write(code);
			out.close();

		} catch (IOException e)
		{
			Logger.getLog().printErrorln("Error when saving class file: "
					+ javaFileName);
			e.printStackTrace();
		}
	}
	
}
