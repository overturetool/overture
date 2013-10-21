package org.overture.pog.tests.framework;

import java.io.File;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.pog.util.PogUtil;
import org.overture.test.framework.results.Result;

public class ClassPpPoTestCase extends PogToStringTestCase
{

	public ClassPpPoTestCase()
	{
		super();

	}

	public ClassPpPoTestCase(File file)
	{
		super(file);
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	public void test() throws Exception
	{
		if (file == null)
		{
			return;
		}
		Result<List<String>> result;
		// try
		// {
		result = convert(PogUtil.pogPp(file));
		compareResults(result, file.getAbsolutePath());
		// } catch (Exception e)
		// {
		// assert false : "Test failed: " + e.getMessage();
		// }
	}

}
