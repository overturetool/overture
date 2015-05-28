/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.LocationAssistantCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Settings;
import org.overture.interpreter.VDMPP;
import org.overture.interpreter.VDMRT;
import org.overture.interpreter.util.ClassListInterpreter;
import org.overture.interpreter.util.ExitStatus;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import de.hunsicker.io.FileFormat;
import de.hunsicker.jalopy.Jalopy;

public class JavaCodeGenUtil
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public static GeneratedData generateJavaFromFiles(List<File> files,
			IRSettings irSettings, JavaSettings javaSettings, Dialect dialect)
			throws AnalysisException, UnsupportedModelingException
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();

		vdmCodGen.setSettings(irSettings);
		vdmCodGen.setJavaSettings(javaSettings);

		return generateJavaFromFiles(files, vdmCodGen, dialect);
	}
	
	public static GeneratedData generateJavaFromFiles(List<File> files,
			JavaCodeGen vdmCodGen, Dialect dialect)
			throws AnalysisException, UnsupportedModelingException
	{
		List<SClassDefinition> mergedParseList = consMergedParseList(files, dialect);

		return generateJavaFromVdm(mergedParseList, vdmCodGen);
	}

	public static List<SClassDefinition> consMergedParseList(List<File> files, Dialect dialect)
			throws AnalysisException
	{
		Settings.dialect = dialect;
		VDMPP vdmrt = (dialect == Dialect.VDM_RT ? new VDMRT() : new VDMPP());
		vdmrt.setQuiet(true);

		ExitStatus status = vdmrt.parse(files);

		if (status != ExitStatus.EXIT_OK)
		{
			throw new AnalysisException("Could not parse files!");
		}

		status = vdmrt.typeCheck();

		if (status != ExitStatus.EXIT_OK)
		{
			throw new AnalysisException("Could not type check files!");
		}

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
		{
			if (vdmClass instanceof AClassClassDefinition) {
				mergedParseList.add(vdmClass);
			}
		}

		return mergedParseList;
	}

	private static GeneratedData generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists, JavaCodeGen vdmCodGen)
			throws AnalysisException, UnsupportedModelingException
	{
		return vdmCodGen.generateJavaFromVdm(mergedParseLists);
	}

	public static Generated generateJavaFromExp(String exp,
			IRSettings irSettings, JavaSettings javaSettings, Dialect dialect)
			throws AnalysisException
	{
		JavaCodeGen vdmCodeGen = new JavaCodeGen();
		vdmCodeGen.setSettings(irSettings);
		vdmCodeGen.setJavaSettings(javaSettings);

		return generateJavaFromExp(exp, vdmCodeGen, dialect);
	}

	public static Generated generateJavaFromExp(String exp,
			JavaCodeGen vdmCodeGen, Dialect dialect)
			throws AnalysisException

	{
		Settings.dialect = dialect;
		TypeCheckResult<PExp> typeCheckResult = GeneralCodeGenUtils.validateExp(exp);

		if (typeCheckResult.errors.size() > 0)
		{
			throw new AnalysisException("Unable to type check expression: "
					+ exp);
		}

		try
		{
			return vdmCodeGen.generateJavaFromVdmExp(typeCheckResult.result);

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

	public static String constructNameViolationsString(
			InvalidNamesResult invalidNames)
	{
		StringBuffer buffer = new StringBuffer();

		List<Violation> reservedWordViolations = asSortedList(invalidNames.getReservedWordViolations());
		List<Violation> typenameViolations = asSortedList(invalidNames.getTypenameViolations());
		List<Violation> tempVarViolations = asSortedList(invalidNames.getTempVarViolations());

		String correctionMessage = String.format("Prefix '%s' has been added to the name"
				+ LINE_SEPARATOR, invalidNames.getCorrectionPrefix());

		for (Violation violation : reservedWordViolations)
		{
			buffer.append("Reserved name violation: " + violation + ". "
					+ correctionMessage);
		}

		for (Violation violation : typenameViolations)
		{
			buffer.append("Type name violation: " + violation + ". "
					+ correctionMessage);
		}

		for (Violation violation : tempVarViolations)
		{
			buffer.append("Temporary variable violation: " + violation + ". "
					+ correctionMessage);
		}

		return buffer.toString();
	}

	public static String constructUnsupportedModelingString(
			UnsupportedModelingException e)
	{
		StringBuffer buffer = new StringBuffer();

		List<Violation> violations = asSortedList(e.getViolations());

		for (Violation violation : violations)
		{
			buffer.append(violation + LINE_SEPARATOR);
		}

		return buffer.toString();
	}
	
	public static String constructVarRenamingString(List<Renaming> renamings)
	{
		StringBuilder sb = new StringBuilder();
		
		for(Renaming r : renamings)
		{
			sb.append(r).append('\n');
		}
		
		return sb.toString();
	}

	public static void generateJavaSourceFiles(File outputFolder,
			List<GeneratedModule> classes)
	{
		JavaCodeGen vdmCodGen = new JavaCodeGen();
		vdmCodGen.generateJavaSourceFiles(outputFolder, classes);
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
			{
				result = b.toString();
			} else if (jalopy.getState() == Jalopy.State.WARN)
			{
				result = code;// formatted with warnings
			} else if (jalopy.getState() == Jalopy.State.ERROR)
			{
				result = code; // could not be formatted
			}

			return result.toString();

		} catch (Exception e)
		{
			Logger.getLog().printErrorln("Could not format code: "
					+ e.toString());
			e.printStackTrace();
		} finally
		{
			tempFile.delete();
		}

		return null;// could not be formatted
	}

	public static void saveJavaClass(File outputFolder, String javaFileName,
			String code)
	{
		try
		{
			File javaFile = new File(outputFolder, File.separator
					+ javaFileName);
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

	public static void printMergeErrors(List<Exception> mergeErrors)
	{
		for (Exception error : mergeErrors)
		{
			Logger.getLog().println(error.toString());
		}
	}

	public static void printUnsupportedIrNodes(Set<VdmNodeInfo> unsupportedNodes)
	{
		AssistantManager assistantManager = new AssistantManager();
		LocationAssistantCG locationAssistant = assistantManager.getLocationAssistant();

		List<VdmNodeInfo> nodesSorted = locationAssistant.getVdmNodeInfoLocationSorted(unsupportedNodes);

		for (VdmNodeInfo vdmNodeInfo : nodesSorted)
		{
			Logger.getLog().print(vdmNodeInfo.getNode().toString() + 
					" (" + vdmNodeInfo.getNode().getClass().getSimpleName() + ")");

			ILexLocation location = locationAssistant.findLocation(vdmNodeInfo.getNode());

			Logger.getLog().print(location != null ? " at [line, pos] = ["
					+ location.getStartLine() + ", " + location.getStartPos()
					+ "]" : "");

			String reason = vdmNodeInfo.getReason();

			if (reason != null)
			{
				Logger.getLog().print(". Reason: " + reason);
			}

			Logger.getLog().println("");
		}
	}
	
	public static void printUnsupportedNodes(Set<IrNodeInfo> unsupportedNodes)
	{
		AssistantManager assistantManager = new AssistantManager();
		LocationAssistantCG locationAssistant = assistantManager.getLocationAssistant();
		
		List<IrNodeInfo> nodesSorted = locationAssistant.getIrNodeInfoLocationSorted(unsupportedNodes);

		for (IrNodeInfo nodeInfo : nodesSorted)
		{
			INode vdmNode = locationAssistant.getVdmNode(nodeInfo);
			Logger.getLog().print(vdmNode != null ? vdmNode.toString() : nodeInfo.getNode().getClass().getSimpleName());

			ILexLocation location = locationAssistant.findLocation(nodeInfo);

			Logger.getLog().print(location != null ? " at [line, pos] = ["
					+ location.getStartLine() + ", " + location.getStartPos()
					+ "]" : "");

			String reason = nodeInfo.getReason();

			if (reason != null)
			{
				Logger.getLog().print(". Reason: " + reason);
			}

			Logger.getLog().println("");
		}
	}
	
	public static boolean isQuote(org.overture.codegen.cgast.INode decl, JavaSettings settings)
	{
		if(decl instanceof AClassDeclCG)
		{
			AClassDeclCG clazz = (AClassDeclCG) decl;
			
			if(clazz.getPackage() == null)
			{
				return false;
			}
			
			String javaPackage = settings.getJavaRootPackage();

			if(javaPackage == null || javaPackage.equals(""))
			{
				return clazz.getPackage().equals(JavaCodeGen.QUOTES);
			}
			
			if(clazz.getPackage().equals(javaPackage + "." + JavaCodeGen.QUOTES))
			{
				return true;
			}
		}
		
		return false;
	}
}
