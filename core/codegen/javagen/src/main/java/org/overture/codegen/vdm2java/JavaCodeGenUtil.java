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

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

public class JavaCodeGenUtil
{
	private static Logger log = Logger.getLogger(JavaCodeGenUtil.class.getName());

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
			JavaCodeGen vdmCodeGen, Dialect dialect) throws AnalysisException

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

		} catch (AnalysisException
				| org.overture.codegen.ir.analysis.AnalysisException e)
		{
			throw new AnalysisException("Unable to generate code from expression: "
					+ exp + ". Exception message: " + e.getMessage());
		}
	}

	public static String formatJavaCode(String code)
	{
		try
		{
			return new Formatter().formatSource(code);
		} catch (FormatterException e)
		{
			log.error("Could not format code: " + e.getMessage());
			e.printStackTrace();
			return code;
		}
	}

	public static boolean isQuote(org.overture.codegen.ir.INode decl,
			JavaSettings settings)
	{
		if (decl instanceof ADefaultClassDeclIR)
		{
			ADefaultClassDeclIR clazz = (ADefaultClassDeclIR) decl;

			if (clazz.getPackage() == null)
			{
				return false;
			}

			String javaPackage = settings.getJavaRootPackage();

			if (javaPackage == null || javaPackage.equals(""))
			{
				return clazz.getPackage().equals(JavaCodeGen.JAVA_QUOTES_PACKAGE);
			}

			if (clazz.getPackage().equals(javaPackage + "."
					+ JavaCodeGen.JAVA_QUOTES_PACKAGE))
			{
				return true;
			}
		}

		return false;
	}

	public static boolean isValidJavaPackage(String pack)
	{
		if (pack == null)
		{
			return false;
		}

		pack = pack.trim();

		Pattern pattern = Pattern.compile("^[a-zA-Z_\\$][\\w\\$]*(?:\\.[a-zA-Z_\\$][\\w\\$]*)*$");

		if (!pattern.matcher(pack).matches())
		{
			return false;
		}

		String[] split = pack.split("\\.");

		for (String s : split)
		{
			if (isJavaKeyword(s))
			{
				return false;
			}
		}

		return true;
	}

	public static String getFolderFromJavaRootPackage(String pack)
	{
		if (!isValidJavaPackage(pack))
		{
			return null;
		} else
		{
			return pack.replaceAll("\\.", "/");
		}
	}

	public static boolean isJavaKeyword(String s)
	{
		if (s == null)
		{
			return false;
		} else
		{
			s = s.trim();

			if (s.isEmpty())
			{
				return false;
			}
		}

		for (String kw : IJavaConstants.RESERVED_WORDS)
		{
			if (s.equals(kw))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks whether the given String is a valid Java identifier.
	 *
	 * @param s
	 *            the String to check
	 * @return <code>true</code> if 's' is an identifier, <code>false</code> otherwise
	 */
	public static boolean isValidJavaIdentifier(String s)
	{
		if (s == null || s.length() == 0)
		{
			return false;
		}

		if (isJavaKeyword(s))
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
	 * @param s
	 *            the identifier to compute correction indices for.
	 * @return the indices of the characters that need to be corrected in order to make the identifier a valid Java
	 *         identifier
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

	public static String[] findJavaFilePathsRec(File srcCodeFolder)
	{
		List<File> files = GeneralUtils.getFilesRecursively(srcCodeFolder);

		List<String> javaFilePaths = new LinkedList<String>();

		for (File f : files)
		{
			if (f.getName().endsWith(IJavaConstants.JAVA_FILE_EXTENSION))
			{
				javaFilePaths.add(f.getAbsolutePath());
			}
		}

		return javaFilePaths.toArray(new String[] {});
	}

	public static File getModuleOutputDir(File outputDir, JavaCodeGen vdmCodGen,
			GeneratedModule generatedClass)
	{
		File moduleOutputDir = outputDir;
		String javaPackage = vdmCodGen.getJavaSettings().getJavaRootPackage();

		if (generatedClass.getIrNode() instanceof SClassDeclIR)
		{
			javaPackage = ((SClassDeclIR) generatedClass.getIrNode()).getPackage();
		} else if (generatedClass.getIrNode() instanceof AInterfaceDeclIR)
		{
			javaPackage = ((AInterfaceDeclIR) generatedClass.getIrNode()).getPackage();
		} else
		{
			log.error("Expected IR node of " + generatedClass.getName()
					+ " to be a class or interface  declaration at this point. Got: "
					+ generatedClass.getIrNode());
			return null;
		}

		if (JavaCodeGenUtil.isValidJavaPackage(javaPackage))
		{
			String packageFolderPath = JavaCodeGenUtil.getFolderFromJavaRootPackage(javaPackage);
			moduleOutputDir = new File(outputDir, packageFolderPath);
		}

		return moduleOutputDir;
	}
}
