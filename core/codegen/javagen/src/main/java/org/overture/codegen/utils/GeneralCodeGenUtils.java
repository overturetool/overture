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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AVoidType;
import org.overture.ast.util.definitions.ClassList;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.IJavaCodeGenConstants;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.Console;
import org.overture.parser.messages.VDMErrorsException;
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
	public static TypeCheckResult<List<SClassDefinition>> validateFile(File file)
			throws AnalysisException
	{
		if (!file.exists() || !file.isFile())
		{
			throw new AnalysisException("Could not find file: "
					+ file.getAbsolutePath());
		}

		ParserResult<List<SClassDefinition>> parseResult = ParserUtil.parseOo(file, "UTF-8");

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

	public static TypeCheckResult<PExp> validateExp(String exp)
			throws AnalysisException
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
		SClassDefinition defaultModule = null;

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
			String line = "", oldtext = "";
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
	
	public static boolean isValidJavaPackage(String pack)
	{
		if(pack == null)
		{
			return false;
		}
		
		pack = pack.trim();
		
		Pattern pattern = Pattern.compile("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$");
		
		if(!pattern.matcher(pack).matches())
		{
			return false;
		}
		
		String [] split = pack.split("\\.");
		
		for(String s : split)
		{
			if(isJavaKeyword(s))
			{
				return false;
			}
		}
		
		return true;
	}
	
	public static String getFolderFromJavaRootPackage(String pack)
	{
		if(!isValidJavaPackage(pack))
		{
			return null;
		}
		else
		{
			return pack.replaceAll("\\.", "/");
		}
	}
	
	public static boolean isJavaKeyword(String s)
	{
		if(s == null)
		{
			return false;
		}
		else
		{
			s = s.trim();
			
			if(s.isEmpty())
			{
				return false;
			}
		}
		
		for(String kw : IJavaCodeGenConstants.RESERVED_WORDS)
		{
			if(s.equals(kw))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks whether the given String is a valid Java identifier.
	 *
	 * @param s the String to check
	 * @return <code>true</code> if 's' is an identifier, <code>false</code> otherwise
	 */
	public static boolean isValidJavaIdentifier(String s)
	{
		if (s == null || s.length() == 0)
		{
			return false;
		}
		
		if(isJavaKeyword(s))
		{
			return false;
		}

		char[] c = s.toCharArray();
		if (!Character.isJavaIdentifierStart(c[0]))
		{
			return false;
		}

		for (int i = 1; i < c.length; i++)
		{
			if (!Character.isJavaIdentifierPart(c[i]))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Computes the indices of characters that need to be replaced with valid characters in order to make 's' a valid
	 * Java identifier. Please note that this method assumes that 's' is NOT a keyword.
	 * 
	 * @param s the identifier to compute correction indices for.
	 * @return the indices of the characters that need to be corrected in order to make the identifier a valid Java
	 * identifier
	 */
	public static List<Integer> computeJavaIdentifierCorrections(String s)
	{
		List<Integer> correctionIndices = new LinkedList<Integer>();

		if (s == null || s.length() == 0)
		{
			return correctionIndices;
		}

		char[] c = s.toCharArray();
		
		if (!Character.isJavaIdentifierStart(c[0]))
		{
			correctionIndices.add(0);
		}

		for (int i = 1; i < c.length; i++)
		{
			if (!Character.isJavaIdentifierPart(c[i]))
			{
				correctionIndices.add(i);
			}
		}

		return correctionIndices;
	}
}
