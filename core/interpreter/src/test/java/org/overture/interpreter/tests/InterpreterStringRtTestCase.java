package org.overture.interpreter.tests;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class InterpreterStringRtTestCase extends InterpreterStringPpTestCase
{
	public InterpreterStringRtTestCase()
	{
		super();

	}

	public InterpreterStringRtTestCase(File file)
	{
		super(file);
	}

	public InterpreterStringRtTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}
	
	public InterpreterStringRtTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
	}


	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;
	}

	@SuppressWarnings("rawtypes")
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
		return "C:\\overtureGit\\overture\\documentation\\examples\\VDMRT";
	}


}
