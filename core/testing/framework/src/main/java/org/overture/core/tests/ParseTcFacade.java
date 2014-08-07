package org.overture.core.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.tests.examples.ExampleAstData;
import org.overture.core.tests.examples.ExampleSourceData;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
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
	/**
	 * Parse and type check a VDM model directly encoded in a String.
	 * 
	 * @param content
	 *            the String representing the VDM model
	 * @param testName
	 *            the name of the test calling this method (used for failure reporting)
	 * @param dialect
	 *            the VDM {@link Dialect} the source is written in
	 * @return the AST of the mode, as a list of {@link INode}
	 * @throws LexException
	 * @throws ParserException
	 */
	public static List<INode> typedAstFromContent(String content,
			String testName, Dialect dialect) throws ParserException,
			LexException
	{
		return typedAst(content, testName, dialect, true);
	}

	/**
	 * Parse and type check an Overture example.
	 * 
	 * @param e
	 *            The {@link ExampleSourceData} of the example to process
	 * @return the {@link ExampleAstData} of the submitted example
	 * @throws ParserException
	 * @throws LexException
	 */
	public static ExampleAstData parseTcExample(ExampleSourceData e)
			throws ParserException, LexException
	{
		List<INode> ast = new LinkedList<INode>();
		Settings.release = e.getRelease();

		ast = ParseTcFacade.typedAst(e.getSource(), e.getName(), e.getDialect(), false);

		return new ExampleAstData(e.getName(), ast);
	}

	/**
	 * Parse and type check a VDM source file.
	 * 
	 * @param sourcePath
	 *            a {@link String} with the path to the VDM model source
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

		if (ext.equals("vdmsl") | ext.equals("vdm"))
		{
			return parseTcSlContent(FileUtils.readFileToString(f), testName, true);
		}

		else
		{
			if (ext.equals("vdmpp") | ext.equals("vpp"))
			{
				return parseTcPpContent(FileUtils.readFileToString(f), testName, true);

			} else
			{
				if (ext.equals("vdmrt"))
				{
					return parseTcRtContent(FileUtils.readFileToString(f), testName, true);
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

	private static List<INode> typedAst(String content, String testName,
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
	// FIXME unify parsing and TCing of VDM dialects
	private static List<INode> parseTcRtContent(String content,
			String testName, boolean retry) throws ParserException,
			LexException
	{
		Settings.dialect = Dialect.VDM_RT;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckRt(content);

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

		assertTrue("Error in test " + testName
				+ " Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Error in test " + testName
				+ " Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcPpContent(String content,
			String testName, boolean retry)
	{
		Settings.dialect = Dialect.VDM_PP;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(content);

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

		assertTrue("Error in test " + testName
				+ " Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Error in test " + testName
				+ " Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSlContent(String content,
			String testName, boolean retry)
	{
		Settings.dialect = Dialect.VDM_SL;

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(content);

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

		assertTrue("Error in test " + testName
				+ " Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Error in test " + testName
				+ " Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

}
