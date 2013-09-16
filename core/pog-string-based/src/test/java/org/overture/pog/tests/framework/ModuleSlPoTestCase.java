package org.overture.pog.tests.framework;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.pog.util.PogUtil;
import org.overture.test.framework.results.Result;

public class ModuleSlPoTestCase extends PogToStringTestCase
{

	public ModuleSlPoTestCase()
	{
		super();

	}

	public ModuleSlPoTestCase(File file)
	{
		super(file);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	public void test() throws ParserException, LexException, IOException
	{
	
		Result<List<String>> result;
		try
		{
			result = convert(PogUtil.pogSl(file));
			compareResults(result, file.getAbsolutePath());
		} catch (Exception e)
		{
			assert false : "Test failed: " + e.getMessage();
		}
	}

	

}
