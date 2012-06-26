package org.overture.interpreter.tests;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class InterpreterPpTestCase extends InterpreterSlTestCase
{
	public InterpreterPpTestCase()
	{
		super();
	}

	public InterpreterPpTestCase(File file)
	{
		super(file);
	}

	public InterpreterPpTestCase(File rootSource, String name, String content)
	{
		super(rootSource, name, content);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected TypeCheckResult typeCheck()
	{
		return TypeCheckerUtil.typeCheckPp(file);
	}

	protected String baseExamplePath()
	{
		return "C:\\overture\\overture_gitAST\\documentation\\examples\\VDM++";
	}

}
