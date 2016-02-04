/*
 * #%~
 * Overture Testing Framework
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
package org.overture.core.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/**
 * Parse and Type Check VDM Sources. This class is the main interaction point with the Overture parser and type checker.
 * It calls on them to process and construct ASTs from various kinds of sources.<br>
 * <br>
 * All methods in this class will cause test failures if they cannot process the source. If you need more fine-grained
 * control over the parsing and type checking processes, we suggest using {@link ParserUtil} and {@link TypeCheckerUtil}
 * .
 * 
 * @author ldc
 */
public abstract class ParseTcFacade
{

	static
	{
		// Omit paths from locations. Allows error comparison on multiple machines.
		LexLocation.absoluteToStringLocation = false;
	}

	public static String UTF8 = "UTF-8";

	/**
	 * Parse and type check a VDM model. This method will try to check the model in VDM classic and VDM 10. It assumes
	 * the model sources are encoded in UTF-8.
	 * 
	 * @param sources
	 *            the {@link List} of {@link File} containing the model's sources
	 * @param testName
	 *            the name of the test calling this method (used for failure reporting)
	 * @param dialect
	 *            the VDM {@link Dialect} the source is written in
	 * @return the AST of the model, as a list of {@link INode}
	 * @throws LexException
	 * @throws ParserException
	 */
	public static List<INode> typedAstWithRetry(List<File> sources,
			String testName, Dialect dialect) throws ParserException,
			LexException
	{
		return typedAst(sources, testName, dialect, true);
	}

	/**
	 * Parse (but don't type check) a VDM model encoded in a string.
	 * 
	 * @param model
	 *            the String representing the model
	 * @param dialect
	 *            the VDM dialect the model is in
	 * @returnthe AST of the model, as a list of {@link INode}
	 */
	public static List<INode> parseModelString(String model, Dialect dialect)
	{
		Settings.dialect = dialect;
		List<INode> r = new LinkedList<INode>();
		switch (dialect)
		{
			case VDM_SL:
				r.addAll(ParserUtil.parseSl(model).result);
				break;
			case VDM_PP:
				r.addAll(ParserUtil.parseOo(model).result);
				break;
			case VDM_RT:
				r.addAll(ParserUtil.parseOo(model).result);
				break;
			default:
				fail("Unrecognised dialect:" + dialect);
				return null;
		}
		return r;
	}

	/**
	 * Parse (but don't type check) a VDM expression encoded in a string.
	 * 
	 * @param expression
	 *            the String representing the expression
	 * @return the {@link PExp} AST node of the expression
	 */
	public static PExp parseExpressionString(String expression)
			throws ParserException, LexException
	{
		return ParserUtil.parseExpression(expression).result;
	}

	/**
	 * Parse and type check a VDM expression encoded in a string using VDM_SL. Failure results in an exception.
	 * 
	 * @param expression
	 *            the String representing the expression
	 * @return the {@link PExp} AST node of the expression
	 * 
	 * @see #parseModelString(String, Dialect)
	 */
	public static PExp parseTcExpressionString(String expression)
			throws ParserException, LexException
	{
		return parseTcExpressionString(expression, Dialect.VDM_SL);
	}

	/**
	 * Parse and type check a VDM expression encoded in a string using the specified dialect. Failure results in an exception.
	 * 
	 * @param expression
	 *            the String representing the expression
	 * @param dialect
	 *            the {@link Dialect} to use when checking
	 * @return the {@link PExp} AST node of the expression
	 */
	public static PExp parseTcExpressionString(String expression,
			Dialect dialect) throws ParserException, LexException,
			TypeCheckException
	{
		Settings.dialect = dialect;
		TypeCheckResult<PExp> res = TypeCheckerUtil.typeCheckExpression(expression);
		if (res.errors.isEmpty())
		{
			return res.result;
		}
		throw new TypeCheckException("Cannot type check string expression.", null, res.result);
	}

	/**
	 * Parse and type check a VDM model encoded in a string.
	 * 
	 * @param model
	 *            the String representing the model
	 * @param dialect
	 *            the VDM dialect the model is in
	 * @returnthe AST of the model, as a list of {@link INode}
	 */
	public static List<INode> typedAstFromString(String model, Dialect dialect)
			throws ParserException, LexException
	{
		Settings.dialect = dialect;
		List<INode> r = new LinkedList<INode>();
		switch (dialect)
		{
			case VDM_SL:
				r.addAll(TypeCheckerUtil.typeCheckSl(model).result);
				break;
			case VDM_PP:
				r.addAll(TypeCheckerUtil.typeCheckPp(model).result);
				break;
			case VDM_RT:
				r.addAll(TypeCheckerUtil.typeCheckRt(model).result);
				break;
			default:
				fail("Unrecognised dialect:" + dialect);
				return null;
		}
		return r;
	}

	/**
	 * Parse and type check a VDM model. This method will check the model in whatever language release is currently set.
	 * It assumes the model sources are encoded in UTF-8.
	 * 
	 * @param sources
	 *            the {@link List} of {@link File} containing the model's sources
	 * @param testName
	 *            the name of the test calling this method (used for failure reporting)
	 * @param dialect
	 *            the VDM {@link Dialect} the source is written in
	 * @return the AST of the model, as a list of {@link INode}
	 * @throws LexException
	 * @throws ParserException
	 */
	public static List<INode> typedAstNoRetry(List<File> sources,
			String testName, Dialect dialect) throws ParserException,
			LexException {
		return typedAst(sources, testName, dialect, false);
	}

	/**
	 * Parse and type check a single VDM source file. It assumes the model source is encoded in UTF-8.
	 * 
	 * @param sourcePath
	 *            a {@link String} with the path to a single VDM model source
	 * @param testName
	 *            the name of the test calling this method (used for failure reporting)
	 * @return the AST of the model as a {@link List} of {@link INode}.
	 * @throws IOException
	 * @throws ParserException
	 * @throws LexException
	 */
	public static List<INode> typedAst(String sourcePath, String testName)
			throws IOException, ParserException, LexException
	{
		String[] parts = sourcePath.split("\\.");
		String ext;
		if (parts.length == 1)
		{
			ext = "vdm"
					+ sourcePath.substring(sourcePath.length() - 2, sourcePath.length()).toLowerCase();
		} else
		{
			ext = parts[1];
		}
		File f = new File(sourcePath);
		List<File> sources = new Vector<File>();
		sources.add(f);

		if (ext.equals("vdmsl") | ext.equals("vdm"))
		{
			return parseTcSlContent(sources, testName, true);
		}

		else
		{
			if (ext.equals("vdmpp") | ext.equals("vpp"))
			{
				List<File> files = new Vector<File>();
				files.add(f);
				return parseTcPpContent(files, testName, true);

			} else
			{
				if (ext.equals("vdmrt"))
				{
					List<File> files = new Vector<File>();
					files.add(f);
					return parseTcRtContent(files, testName, true);
				} else
				{
					fail("Unexpected extension in file " + sourcePath
							+ ". Only .vdmpp, .vdmsl and .vdmrt allowed");
				}
			}
		}

		// only needed to compile. will never hit because of fail()
		return null;
	}

	private static List<INode> typedAst(List<File> content, String testName,
			Dialect dialect, boolean retry) throws ParserException,
			LexException
	{

		switch (dialect)
		{
			case VDM_SL:
				return parseTcSlContent(content, testName, retry);
			case VDM_PP:
				return parseTcPpContent(content, testName, retry);
			case VDM_RT:
				return parseTcRtContent(content, testName, retry);
			default:
				fail("Unrecognised dialect:" + dialect);
				return null;
		}

	}

	// These 3 methods have so much duplicated code because we cannot
	// return the TC results since their types are all different.
	private static List<INode> parseTcRtContent(List<File> content,
			String testName, boolean retry) throws ParserException,
			LexException
	{
		Settings.dialect = Dialect.VDM_RT;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckRt(content, UTF8);

		// retry with other dialect
		if (retry
				&& (!TC.parserResult.errors.isEmpty() || !TC.errors.isEmpty()))
		{
			if (Settings.release == Release.CLASSIC)
			{
				Settings.release = Release.VDM_10;
				return parseTcRtContent(content, testName, false);
			}
			if (Settings.release == Release.VDM_10)
			{
				Settings.release = Release.CLASSIC;
				return parseTcRtContent(content, testName, false);
			}
		}

		checkTcResult(TC);

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcPpContent(List<File> content,
			String testName, boolean retry)
	{
		Settings.dialect = Dialect.VDM_PP;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(content, UTF8);

		// retry with other dialect
		if (retry
				&& (!TC.parserResult.errors.isEmpty() || !TC.errors.isEmpty()))
		{
			if (Settings.release == Release.CLASSIC)
			{
				Settings.release = Release.VDM_10;
				return parseTcPpContent(content, testName, false);
			}
			if (Settings.release == Release.VDM_10)
			{
				Settings.release = Release.CLASSIC;
				return parseTcPpContent(content, testName, false);
			}
		}

		checkTcResult(TC);

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSlContent(List<File> content,
			String testName, boolean retry)
	{
		Settings.dialect = Dialect.VDM_SL;

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(content, UTF8);

		// retry with other dialect
		if (retry
				&& (!TC.parserResult.errors.isEmpty() || !TC.errors.isEmpty()))
		{
			if (Settings.release == Release.CLASSIC)
			{
				Settings.release = Release.VDM_10;
				return parseTcSlContent(content, testName, false);
			}
			if (Settings.release == Release.VDM_10)
			{
				Settings.release = Release.CLASSIC;
				return parseTcSlContent(content, testName, false);
			}
		}

		checkTcResult(TC);

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	protected static void checkTcResult(
			@SuppressWarnings("rawtypes") TypeCheckResult TC)
	{
		assertTrue("Parse Error:\n" + TC.parserResult.getErrorString(), TC.parserResult.errors.isEmpty());
		assertTrue("Type Check Error:\n" + TC.getErrorString(), TC.errors.isEmpty());
	}

}
