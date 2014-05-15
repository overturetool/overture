package org.overture.pog.tests.framework;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.util.PogUtil;
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
		try
		{
			configureResultGeneration();
			if (file == null)
			{
				return;
			}

			Result<ProofObligationList> result;
			result = convert(PogUtil.pogSl(file));
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
