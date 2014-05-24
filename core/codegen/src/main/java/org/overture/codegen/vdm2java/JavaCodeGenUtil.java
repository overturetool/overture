package org.overture.codegen.vdm2java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
import org.overture.codegen.logging.Logger;
import org.overture.codegen.ooast.OoAstSettings;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.interpreter.VDMPP;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;

public class JavaCodeGenUtil
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static GeneratedData generateJavaFromFiles(List<File> files, OoAstSettings settings) throws AnalysisException, InvalidNamesException, UnsupportedModelingException
	{
		List<SClassDefinition> mergedParseList = consMergedParseList(files);
		
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		
		vdmCodGen.setSettings(settings);

		List<GeneratedModule> generatedModules = generateJavaFromVdm(mergedParseList, vdmCodGen);
		
		GeneratedModule quoteValues = vdmCodGen.generateJavaFromVdmQuotes();
		
		GeneratedData dataToReturn = new GeneratedData(generatedModules, quoteValues);
		
		return dataToReturn;
	}
	
	public static List<SClassDefinition> consMergedParseList(List<File> files) throws AnalysisException
	{
		VDMPP vdmrt = new VDMPP();
		vdmrt.setQuiet(true);
		
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
		
		List<SClassDefinition> mergedParseList = new LinkedList<SClassDefinition>();
		
		for (SClassDefinition vdmClass : classes)
			mergedParseList.add(vdmClass);

		return mergedParseList;
	}

	private static List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists, JavaCodeGen vdmCodGen) throws AnalysisException, InvalidNamesException, UnsupportedModelingException
	{
		return vdmCodGen.generateJavaFromVdm(mergedParseLists);
	}

	public static Generated generateJavaFromExp(String exp, OoAstSettings settings) throws AnalysisException
	{
		TypeCheckResult<PExp> typeCheckResult = GeneralCodeGenUtils.validateExp(exp);
		
		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp);
		}

		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.setSettings(settings);
		
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
					+ LINE_SEPARATOR);
		}

		for (Violation violation : typenameViolations)
		{
			buffer.append("Type name violation: " + violation + LINE_SEPARATOR);
		}
		
		for(Violation violation : tempVarViolations)
		{
			buffer.append("Temporary variable violation: " + violation + LINE_SEPARATOR);
		}
		
		return buffer.toString();
	}
	
	public static String constructUnsupportedModelingString(UnsupportedModelingException e)
	{
		StringBuffer buffer = new StringBuffer();
		
		List<Violation> violations = asSortedList(e.getViolations());
		
		for (Violation violation : violations)
		{
			buffer.append(violation + LINE_SEPARATOR);
		}
		
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
		File tempFile = null;
		StringBuffer b = new StringBuffer();
		try
		{
			tempFile = new File("target" + File.separatorChar + "temp.java");
			tempFile.getParentFile().mkdirs();
			tempFile.createNewFile();
			
			PrintWriter xwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(tempFile, false), "UTF-8"));
			xwriter.write(code.toString());
			xwriter.flush();

			Jalopy jalopy = new Jalopy();
			jalopy.setFileFormat(FileFormat.DEFAULT);
			jalopy.setInput(tempFile);
			jalopy.setOutput(b);
			jalopy.format();

			xwriter.close();

			String result = null;
			
			if (jalopy.getState() == Jalopy.State.OK
					|| jalopy.getState() == Jalopy.State.PARSED)
				result = b.toString();
			else if (jalopy.getState() == Jalopy.State.WARN)
				result = code;// formatted with warnings
			else if (jalopy.getState() == Jalopy.State.ERROR)
				 result = code; // could not be formatted
			
			return result.toString();

		} catch (Exception e)
		{
			Logger.getLog().printErrorln("Could not format code: "
					+ e.toString());
			e.printStackTrace();
		}
		finally
		{
			tempFile.delete();
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
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(javaFile, false), "UTF-8"));
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
