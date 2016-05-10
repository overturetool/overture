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
package org.overture.codegen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AVoidType;
import org.overture.ast.util.definitions.ClassList;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.LocationAssistantIR;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.Console;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.ClassTypeChecker;
import org.overture.typechecker.Environment;
import org.overture.typechecker.PublicClassEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;
import org.overture.typechecker.visitor.TypeCheckVisitor;

public class GeneralCodeGenUtils
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public static boolean isVdmSourceFile(File f)
	{
		return isVdmPpSourceFile(f) || isVdmSlSourceFile(f) || isVdmRtSourceFile(f);
	}
	
	public static boolean isVdmRtSourceFile(File f)
	{
		return hasExtension(f, new String[]{".vdmrt", ".vrt"});
	}
	
	public static boolean isVdmSlSourceFile(File f)
	{
		return hasExtension(f, new String[]{".vsl", ".vdmsl"});
	}
	
	public static boolean isVdmPpSourceFile(File f)
	{
		return hasExtension(f, new String[]{".vdmpp", ".vpp"});
	}

	private static boolean hasExtension(File f, String[] extensions)
	{
		for(String ext : extensions)
		{
			if(f.getName().endsWith(ext))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static String errorStr(TypeCheckResult<?> tcResult)
	{
		if(tcResult == null)
		{
			return "No type check result found!";
		}
		
		StringBuilder sb = new StringBuilder();
		
		if(!tcResult.parserResult.warnings.isEmpty())
		{
			sb.append("Parser warnings:").append('\n');
			for(VDMWarning w : tcResult.parserResult.warnings)
			{
				sb.append(w).append('\n');
			}
			sb.append('\n');
		}
		
		if(!tcResult.parserResult.errors.isEmpty())
		{
			sb.append("Parser errors:").append('\n');
			for(VDMError e : tcResult.parserResult.errors)
			{
				sb.append(e).append('\n');
			}
			sb.append('\n');
		}
		
		if(!tcResult.warnings.isEmpty())
		{
			sb.append("Type check warnings:").append('\n');
			for(VDMWarning w : tcResult.warnings)
			{
				sb.append(w).append('\n');
			}
			sb.append('\n');
		}
		
		if(!tcResult.errors.isEmpty())
		{
			sb.append("Type check errors:").append('\n');
			for(VDMError w : tcResult.errors)
			{
				sb.append(w).append('\n');
			}
			sb.append('\n');
		}
		
		return sb.toString();
	}

	public static TypeCheckResult<PExp> validateExp(String exp)
			throws AnalysisException
	{
		if (exp == null || exp.isEmpty())
		{
			throw new AnalysisException("No expression to generate from");
		}

		ParserResult<PExp> parseResult;
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

		TypeCheckResult<PExp> typeCheckResult;
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
	
	public static boolean hasErrors(TypeCheckResult<?> tcResult)
	{
		return !tcResult.parserResult.errors.isEmpty() || !tcResult.errors.isEmpty();
	}

	public static SClassDefinition consMainClass(
			List<SClassDefinition> mergedParseLists, String expression,
			Dialect dialect, String mainClassName, ITempVarGen nameGen) throws VDMErrorsException, AnalysisException
	{
		ClassList classes = new ClassList();
		classes.addAll(mergedParseLists);
		PExp entryExp = typeCheckEntryPoint(classes, expression, dialect);

		String resultTypeStr = entryExp.getType() instanceof AVoidType ? "()"
				: "?";

		// Collect all the class names
		List<String> namesToAvoid = new LinkedList<>();

		for (SClassDefinition c : classes)
		{
			namesToAvoid.add(c.getName().getName());
		}

		// If the user already uses the name proposed for the main class
		// we have to find a new name for the main class
		if (namesToAvoid.contains(mainClassName))
		{
			String prefix = mainClassName + "_";
			mainClassName = nameGen.nextVarName(prefix);

			while (namesToAvoid.contains(mainClassName))
			{
				mainClassName = nameGen.nextVarName(prefix);
			}
		}

		String entryClassTemplate = "class " + mainClassName + "\n"
				+ "operations\n" + "public static Run : () ==> "
				+ resultTypeStr + "\n" + "Run () == " + expression + ";\n"
				+ "end " + mainClassName;

		SClassDefinition clazz = parseClass(entryClassTemplate, mainClassName, dialect);

		return tcClass(classes, clazz);
	}
	
	public static PExp typeCheckEntryPoint(ClassList classes, String expression, Dialect dialect)
			throws VDMErrorsException, AnalysisException
	{
		SClassDefinition defaultModule;

		LexNameToken name =new LexNameToken("CLASS", "DEFAULT", new LexLocation());
		defaultModule = AstFactory.newAClassClassDefinition(name, null, null);
		defaultModule.setUsed(true);
		PExp exp = parseExpression(expression, defaultModule.getName().getName(),dialect);

		return tcExp(classes, exp);
	}
	
	public static PExp tcExp(ClassList classes, PExp exp)
			throws AnalysisException, VDMErrorsException
	{
		TypeCheckerAssistantFactory af = new TypeCheckerAssistantFactory();
		ClassTypeChecker.clearErrors();
		ClassTypeChecker classTc = new ClassTypeChecker(classes, af);

		for(SClassDefinition c : classes)
		{
			clearTypeData(c);
		}
		
		classTc.typeCheck();

		TypeCheckVisitor tc = new TypeCheckVisitor();
		TypeChecker.clearErrors();
		Environment env = new PublicClassEnvironment(af, classes, null);

		exp.apply(tc, new TypeCheckInfo(af, env, NameScope.NAMESANDSTATE));

		if (TypeChecker.getErrorCount() > 0)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());
		}
		else
		{
			return exp;
		}
	}

	public static SClassDefinition tcClass(ClassList classes,
			SClassDefinition clazz) throws AnalysisException,
			VDMErrorsException

	{
		for(SClassDefinition c : classes)
		{
			clearTypeData(c);
		}
		
		TypeCheckerAssistantFactory af = new TypeCheckerAssistantFactory();
		ClassTypeChecker.clearErrors();
		ClassTypeChecker classTc = new ClassTypeChecker(classes, af);

		classes.add(clazz);
		classTc.typeCheck();
		
		if (TypeChecker.getErrorCount() > 0)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());
		}
		else
		{
			return clazz;
		}
	}

	private static void clearTypeData(SClassDefinition c) throws AnalysisException
	{
		// Reset lex name data so the classes can be type checked again
		c.apply(new DepthFirstAnalysisAdaptor()
		{
			@Override
			public void inILexNameToken(ILexNameToken node) throws AnalysisException
			{
				if(node instanceof LexNameToken && node.parent() != null)
				{
					((LexNameToken) node).typeQualifier = null;
					node.parent().replaceChild(node, node.copy());
				}
			}
		});
	}
	
	public static PExp parseExpression(String expression,
			String defaultModuleName, Dialect dialect) throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(expression, dialect, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(defaultModuleName);
		
		return reader.readExpression();
	}
	
	public static SClassDefinition parseClass(String classStr,
			String defaultModuleName, Dialect dialect)
	{
		LexTokenReader ltr = new LexTokenReader(classStr, dialect, Console.charset);
		ClassReader reader = new ClassReader(ltr);
		reader.setCurrentModule(defaultModuleName);
		
		// There should be only one class
		for(SClassDefinition clazz : reader.readClasses())
		{
			if(clazz.getName().getName().equals(defaultModuleName))
			{
				return clazz;
			}
		}
		
		return null;
	}

	public static void replaceInFile(File file, String regex, String replacement)
	{
		replaceInFile(file.getAbsolutePath(), regex, replacement);
	}

	public static void replaceInFile(String filePath, String regex,
			String replacement)
	{
		try
		{
			File file = new File(filePath);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line, oldtext = "";
			while ((line = reader.readLine()) != null)
			{
				oldtext += line + "\n";
			}
			reader.close();
			String newtext = oldtext.replaceAll(regex, replacement);

			FileWriter writer = new FileWriter(filePath);
			writer.write(newtext);
			writer.close();
		} catch (IOException ioe)
		{
			Logger.getLog().printErrorln("Error replacing characters in file: "
					+ filePath);
			ioe.printStackTrace();
		}
	}

	public static void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException
	{
		if (!targetLocation.exists())
		{
			targetLocation.getParentFile().mkdirs();
		}

		if (sourceLocation.isDirectory())
		{
			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++)
			{
				copyDirectory(new File(sourceLocation, children[i]), new File(targetLocation, children[i]));
			}
		} else
		{
			targetLocation.createNewFile();

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static List<String> getClassesToSkip(String userInput)
	{
		if(userInput == null)
		{
			return new LinkedList<String>();
		}
		
		String[] split = userInput.split(";");

		List<String> classesToSkip = new LinkedList<String>();
		
		for(String element : split)
		{
			element = element.trim();
			
			if(element != null && !element.isEmpty())
			{
				if(!classesToSkip.contains(element))
				{
					classesToSkip.add(element);
				}
			}
		}
		
		return classesToSkip;
	}
	
	public static String constructNameViolationsString(
			InvalidNamesResult invalidNames)
	{
		StringBuffer buffer = new StringBuffer();

		List<Violation> reservedWordViolations = asSortedList(invalidNames.getReservedWordViolations());
		List<Violation> typenameViolations = asSortedList(invalidNames.getTypenameViolations());
		List<Violation> tempVarViolations = asSortedList(invalidNames.getTempVarViolations());
		List<Violation> objectMethodViolations = asSortedList(invalidNames.getObjectMethodViolations());

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
		
		for (Violation violation : objectMethodViolations)
		{
			buffer.append("java.lang.Object method violation: " + violation + ". "
					+ correctionMessage);
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
	
	public static List<Violation> asSortedList(Set<Violation> violations)
	{
		LinkedList<Violation> list = new LinkedList<Violation>(violations);
		Collections.sort(list);

		return list;
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
		LocationAssistantIR locationAssistant = assistantManager.getLocationAssistant();

		List<VdmNodeInfo> nodesSorted = locationAssistant.getVdmNodeInfoLocationSorted(unsupportedNodes);

		for (VdmNodeInfo vdmNodeInfo : nodesSorted)
		{
			Logger.getLog().print(vdmNodeInfo.getNode().toString() + 
					" (" + vdmNodeInfo.getNode().getClass().getSimpleName() + ")");

			ILexLocation location = locationAssistant.findLocation(vdmNodeInfo.getNode());

			Logger.getLog().print(location != null ? " at [line, pos] = ["
					+ location.getStartLine() + ", " + location.getStartPos()
					+ "] in " + location.getFile().getName() : "");

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
		LocationAssistantIR locationAssistant = assistantManager.getLocationAssistant();
		
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
}
