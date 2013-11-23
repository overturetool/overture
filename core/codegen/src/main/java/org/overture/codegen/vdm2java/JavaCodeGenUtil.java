package org.overture.codegen.vdm2java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.utils.InvalidNamesException;
import org.overture.codegen.utils.NameViolation;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;

public class JavaCodeGenUtil
{
	public static List<GeneratedModule> generateJava(File file) throws AnalysisException, InvalidNamesException
	{
		return generateJava(file, new JavaCodeGen());
	}
	
	public static List<GeneratedModule> generateJava(File file, JavaCodeGen vdmCodGen) throws AnalysisException, InvalidNamesException
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
	
	public static GeneratedData generateJavaFromFiles(List<File> files) throws AnalysisException, InvalidNamesException
	{
		VDMRT vdmrt = new VDMRT();
		
		ExitStatus status = vdmrt.parse(files);
		
		if(status != ExitStatus.EXIT_OK)
			throw new AnalysisException("Could not parse files!");
		
		status = vdmrt.typeCheck();

		if(status != ExitStatus.EXIT_OK)
			throw new AnalysisException("Could not type check files!");
		
		ClassListInterpreter classes;
		try
		{
			classes = vdmrt.getInterpreter().getClasses();
		} catch (Exception e)
		{
			throw new AnalysisException("Could not get classes from class list interpreter!");
		}
		
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		List<SClassDefinition> mergedParseList = new LinkedList<SClassDefinition>();
		
		for (SClassDefinition vdmClass : classes)
			mergedParseList.add(vdmClass);
		
		List<GeneratedModule> generatedModules = generateJavaFromVdm(mergedParseList, vdmCodGen);
		
		GeneratedModule quoteValues = vdmCodGen.generateJavaFromVdmQuotes();
		
		GeneratedData dataToReturn = new GeneratedData(generatedModules, quoteValues);
		
		return dataToReturn;
		
	}

	public static List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists) throws AnalysisException, InvalidNamesException
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		return vdmCodGen.generateJavaFromVdm(mergedParseLists);
	}
	
	
	public static List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists, JavaCodeGen vdmCodGen) throws AnalysisException, InvalidNamesException
	{
		return vdmCodGen.generateJavaFromVdm(mergedParseLists);
	}

	
	public static GeneratedData generateJavaFromFile(File file) throws AnalysisException, InvalidNamesException
	{
		return generateJavaFromFiles(new String[]{"", file.getAbsolutePath()});
	}
	
	public static GeneratedData generateJavaFromFiles(String[] args) throws AnalysisException, InvalidNamesException
	{		
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		List<GeneratedModule> data = new ArrayList<GeneratedModule>();
		
		for (int i = 1; i < args.length; i++)
		{
			String fileName = args[i];
			File file = new File(fileName);
			data.addAll(generateJava(file, vdmCodGen));
		}
		
		GeneratedModule quoteValues = vdmCodGen.generateJavaFromVdmQuotes();
		
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
	
	public static String constructNameViolationsString(InvalidNamesException e)
	{
		StringBuffer buffer = new StringBuffer();
		
		List<NameViolation> reservedWordViolations = e.getReservedWordViolations();
		List<NameViolation> typenameViolations = e.getTypenameViolations();
		
		if (!reservedWordViolations.isEmpty())
		{	
			for (NameViolation violation : reservedWordViolations)
			{
				buffer.append("Reserved name violation: " + violation + IText.NEW_LINE);
			}
		}
		
		if (!typenameViolations.isEmpty())
		{	
			for (NameViolation violation : typenameViolations)
			{
				buffer.append("Type name violation: " + violation + IText.NEW_LINE);
			}
		}
		
		int lastIndex = buffer.lastIndexOf(IText.NEW_LINE);
		
		if(lastIndex >= 0)
			buffer.replace(lastIndex, lastIndex + IText.NEW_LINE.length(), "");
		
		return buffer.toString();
	}
	
	public static void generateJavaSourceFiles(File file, List<GeneratedModule> classes)
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.generateJavaSourceFiles(file, classes);
	}
	
	public static void generateJavaSourceFile(File file, GeneratedModule module)
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.generateJavaSourceFile(file, module);
	}
	
	public static List<GeneratedModule> generateJavaCodeGenUtils() throws IOException
	{
		JavaCodeGen vdmCodeGen = new JavaCodeGen();
		return vdmCodeGen.generateJavaCodeGenUtils();
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
				return code;// formatted with warnings
			else if (jalopy.getState() == Jalopy.State.ERROR)
				return code; // could not be formatted

		} catch (Exception e)
		{
			Logger.getLog().printErrorln("Could not format code: "
					+ e.toString());
			e.printStackTrace();
		}

		return null;// could not be formatted
	}
	
	public static void saveJavaClass(File outputFolder, String javaFileName, String code)
	{
		try
		{
			File javaFile = new File(outputFolder, File.separator + javaFileName);
			javaFile.getParentFile().mkdirs();
			javaFile.createNewFile();
			FileWriter writer = new FileWriter(javaFile);
			BufferedWriter out = new BufferedWriter(writer);
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
