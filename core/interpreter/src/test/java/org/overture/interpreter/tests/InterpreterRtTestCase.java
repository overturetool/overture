package org.overture.interpreter.tests;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class InterpreterRtTestCase extends InterpreterSlTestCase
{
	public InterpreterRtTestCase()
	{
		super();
	}

	public InterpreterRtTestCase(File file)
	{
		super(file);
	}

	public InterpreterRtTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected TypeCheckResult typeCheck()
	{
		try
		{
			return TypeCheckerUtil.typeCheckRt(file);
		} catch (ParserException e)
		{
		} catch (LexException e)
		{
		}
		return null;
	}

	protected String baseExamplePath()
	{
		return "C:\\overture\\overture_gitAST\\documentation\\examples\\VDM++";
	}

}
