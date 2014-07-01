package org.overture.core.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.tools.examplepackager.util.ExampleTestData;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

/**
 * Helper Class for the new test framework. Helps process test inputs. This class handles I/O and interaction with the
 * Overture TC and Parser.
 * 
 * @author ldc
 */
public class InputProcessor
{

	/**
	 * Parse and type check a VDM model directly encoded in a String.
	 * 
	 * @param content
	 * @return
	 */
	public static List<INode> typedAstFromContent(String content,
			Dialect dialect, Release release)
	{
		Settings.release = release;

		switch (dialect)
		{
			case VDM_SL:
				return parseTcSlContent(content);
			case VDM_PP:
				return parseTcPpContent(content);
			case VDM_RT:
				return parseTcRtContent(content);
			default:
				fail("Unrecognised dialect:" + dialect);
				return null;
		}

	}

	/**
	 * Parse and type check an Overture example
	 * 
	 * @param e
	 *            The {@link ExampleTestData} of the example to process
	 * @return the {@link ExampleAstData} of the submitted example
	 * @throws ParserException
	 * @throws LexException
	 */
	public static ExampleAstData parseExample(ExampleTestData e)
			throws ParserException, LexException
	{
		List<INode> ast = new LinkedList<INode>();
		// convert between enums. weee!
		Release rel = Release.CLASSIC;

		if (e.getRelease() == org.overture.tools.examplepackager.Release.VDM_10)
		{
			rel = org.overture.config.Release.VDM_10;
		}

		switch (e.getDialect())
		{
			case VDM_SL:
				ast = InputProcessor.typedAstFromContent(e.getSource(), Dialect.VDM_SL, rel);
				break;
			case VDM_PP:
				ast = InputProcessor.typedAstFromContent(e.getSource(), Dialect.VDM_PP, rel);
				break;
			case VDM_RT:
				ast = InputProcessor.typedAstFromContent(e.getSource(), Dialect.VDM_RT, rel);
				break;
			default:
				fail("Unrecognised dialect:" + e.getDialect());
				break;
		}
		return new ExampleAstData(e.getName(), ast);
	}

	private static List<INode> parseTcRtContent(String content)
	{
		Settings.dialect = Dialect.VDM_PP;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(content);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcPpContent(String content)
	{
		Settings.dialect = Dialect.VDM_PP;

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(content);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSlContent(String content)
	{
		Settings.dialect = Dialect.VDM_SL;

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(content);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	/**
	 * Parse and type check a VDM source file and return the model's AST.
	 * 
	 * @param sourcePath
	 *            a {@link String} with the path to the VDM model source
	 * @return the AST of the model as a {@link List} of {@link INode}.
	 */
	public static List<INode> typedAst(String sourcePath) throws IOException,
			ParserException, LexException
	{
		String ext = sourcePath.split("\\.")[1];

		boolean switchRelease = false;
		try
		{
			if (sourcePath.contains("vdm10release"))
			{
				switchRelease = true;
				Settings.release = Release.VDM_10;
			}

			if (ext.equals("vdmsl"))
			{
				return parseTcSl(sourcePath);
			}

			else
			{
				if (ext.equals("vdmpp"))
				{
					return parseTcPp(sourcePath);
				} else
				{
					if (ext.equals("vdmrt"))
					{
						return parseTcRt(sourcePath);
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
	private static List<INode> parseTcPp(String name)
	{
		Settings.dialect = Dialect.VDM_PP;
		File f = new File(name);

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckPp(f);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcSl(String name)
	{
		Settings.dialect = Dialect.VDM_SL;
		File f = new File(name);

		TypeCheckResult<List<AModuleModules>> TC = TypeCheckerUtil.typeCheckSl(f);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

	private static List<INode> parseTcRt(String name) throws ParserException,
			LexException
	{
		Settings.dialect = Dialect.VDM_RT;
		File f = new File(name);

		TypeCheckResult<List<SClassDefinition>> TC = TypeCheckerUtil.typeCheckRt(f);

		assertTrue("Specification has parse errors", TC.parserResult.errors.isEmpty());
		assertTrue("Specification has type errors", TC.errors.isEmpty());

		List<INode> r = new LinkedList<INode>();
		r.addAll(TC.result);

		return r;
	}

}
