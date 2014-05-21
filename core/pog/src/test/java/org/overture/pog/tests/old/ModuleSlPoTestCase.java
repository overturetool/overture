package org.overture.pog.tests.old;

import java.io.File;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.utility.PogUtil;
import org.overture.test.framework.results.Result;

public class ModuleSlPoTestCase extends PogTestCase
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

	public void test() throws Exception
	{
		//TODO continue POG SL test checks one by one
		try
		{
			configureResultGeneration();
			if (file == null)
			{
				return;
			}

			Result<List<String>> result;
			result = convert(TestPogUtil.pogSl(file));
			compareResults(result, file.getAbsolutePath());
		} finally
		{
			unconfigureResultGeneration();
		}
	}

	@Override
	protected String getPropertyId()
	{
		return "sl";
	}

}
