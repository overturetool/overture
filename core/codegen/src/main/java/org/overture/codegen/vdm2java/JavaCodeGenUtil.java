package org.overture.codegen.vdm2java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.analysis.violations.InvalidNamesException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;

public class JavaCodeGenUtil
{
	public static GeneratedData generateJavaFromFiles(List<File> files) throws AnalysisException, InvalidNamesException, UnsupportedModelingException
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

	private static List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists, JavaCodeGen vdmCodGen) throws AnalysisException, InvalidNamesException, UnsupportedModelingException
	{
		return vdmCodGen.generateJavaFromVdm(mergedParseLists);
	}

	public static Generated generateJavaFromExp(String exp) throws AnalysisException
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
	
	public static List<Violation> asSortedList(Set<Violation> violations)
	{
		LinkedList<Violation> list = new LinkedList<Violation>(violations);
		Collections.sort(list);
		
		return list;
	}
	
	public static String constructNameViolationsString(InvalidNamesException e)
	{
		StringBuffer buffer = new StringBuffer();
		
		List<Violation> reservedWordViolations = asSortedList(e.getReservedWordViolations());
		List<Violation> typenameViolations = asSortedList(e.getTypenameViolations());
		List<Violation> tempVarViolations = asSortedList(e.getTempVarViolations());
		
		for (Violation violation : reservedWordViolations)
		{
			buffer.append("Reserved name violation: " + violation
					+ IText.NEW_LINE);
		}

		for (Violation violation : typenameViolations)
		{
			buffer.append("Type name violation: " + violation + IText.NEW_LINE);
		}
		
		for(Violation violation : tempVarViolations)
		{
			buffer.append("Temporary variable violation: " + violation + IText.NEW_LINE);
		}
		
		int lastIndex = buffer.lastIndexOf(IText.NEW_LINE);
		
		if(lastIndex >= 0)
			buffer.replace(lastIndex, lastIndex + IText.NEW_LINE.length(), "");
		
		return buffer.toString();
	}
	
	public static String constructUnsupportedModelingString(UnsupportedModelingException e)
	{
		StringBuffer buffer = new StringBuffer();
		
		List<Violation> violations = asSortedList(e.getViolations());
		
		for (Violation violation : violations)
		{
			buffer.append(violation + IText.NEW_LINE);
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
