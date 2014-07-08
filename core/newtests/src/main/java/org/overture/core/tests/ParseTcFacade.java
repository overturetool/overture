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
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/**
 * Helper Class for the new test framework. Responsible for processing and constructing test inputs. It construcs typed
 * ASTs from various kinds of sources.<br>
 * <br>
 * This class handles I/O and interactions with the Overture TC and Parser.
 * 
 * @author ldc
 */
public class ParseTcFacade
{

	/**
	 * Parse and type check a VDM model directly encoded in a String.
	 * 
	 * @param content
	 * @return
	 * @throws LexException 
	 * @throws ParserException 
	 */
	public static List<INode> typedAstFromContent(String content,
			String testName, Dialect dialect, Release release) throws ParserException, LexException
	{
		Settings.release = release;

		switch (dialect)
		{
			case VDM_SL:
				return parseTcSlContent(content, testName);
			case VDM_PP:
				return parseTcPpContent(content, testName);
			case VDM_RT:
				return parseTcRtContent(content, testName);
			default:
				fail("Unrecognised dialect:" + dialect);
				return null;
		}

	}

	/**
	 * Parse and type check an Overture example
	 * 
	 * @param e
	 *            The {@link ExampleSourceData} of the example to process
	 * @return the {@link ExampleAstData} of the submitted example
	 * @throws ParserException
	 * @throws LexException
	 */
	public static ExampleAstData parseExample(ExampleSourceData e)
			throws ParserException, LexException
	{
		List<INode> ast = new LinkedList<INode>();

		switch (e.getDialect())
		{
			case VDM_SL:
				ast = ParseTcFacade.typedAstFromContent(e.getSource(), e.getName(), Dialect.VDM_SL, e.getRelease());
				break;
			case VDM_PP:
				ast = ParseTcFacade.typedAstFromContent(e.getSource(), e.getName(), Dialect.VDM_PP, e.getRelease());
				break;
			case VDM_RT:
				ast = ParseTcFacade.typedAstFromContent(e.getSource(), e.getName(), Dialect.VDM_RT, e.getRelease());
				break;
			default:
				fail("Unrecognised dialect:" + e.getDialect());
				break;
		}
		return new ExampleAstData(e.getName(), ast);
	}

	/**
	 * Parse and type check a VDM source file and return the model's AST.
	 * 
	 * @param sourcePath
	 *            a {@link String} with the path to the VDM model source
	 * @param testName
	 * @return the AST of the model as a {@link List} of {@link INode}.
	 */
	public static List<INode> typedAst(String sourcePath, String testName)
			throws IOException, ParserException, LexException
	{
		String ext = sourcePath.split("\\.")[1];
		File f = new File(sourcePath);

		boolean switchRelease = false;
		try
		{
			if (sourcePath.contains("vdm10release"))
			{
				switchRelease = true;
				Settings.release = Release.VDM_10;
			}

			if (ext.equals("vdmsl") | ext.equals("vdm"))
			{
				return parseTcSlContent(FileUtils.readFileToString(f), testName);
			}

			else
			{
				if (ext.equals("vdmpp") | ext.equals("vpp"))
				{
					return parseTcPpContent(FileUtils.readFileToString(f), testName);

				} else
				{
					if (ext.equals("vdmrt"))
					{
						return parseTcRtContent(FileUtils.readFileToString(f), testName);
					} else
					{
						fail("Unexpected extension in file " + sourcePath
								+ ". Only .vdmpp, .vdmsl and .vdmrt allowed");
					}
				}
			}
		} finally
		{
			if (switchRelease)
			{
				Settings.release = Release.DEFAULT;
			}
		}
		// only needed to compile. will never hit because of fail()
		return null;
	}

	// These 3 methods have so much duplicated code because we cannot
	// return the TC results since their types are all different.
	// FIXME unify parsing and TCing of VDM dialects
	private static List<INode> parseTcRtContent(String content, String testName) throws ParserException, LexException
	{
		Settings.dialect = Dialect.VDM_RT;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckRt(content);

		assertTrue("Error in test " + testName
				+ " Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Error in test " + testName
				+ " Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcPpContent(String content, String testName)
	{
		Settings.dialect = Dialect.VDM_PP;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(content);

		assertTrue("Error in test " + testName
				+ " Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Error in test " + testName
				+ " Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSlContent(String content, String testName)
	{
		Settings.dialect = Dialect.VDM_SL;

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(content);

		assertTrue("Error in test " + testName
				+ " Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Error in test " + testName
				+ " Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

}
