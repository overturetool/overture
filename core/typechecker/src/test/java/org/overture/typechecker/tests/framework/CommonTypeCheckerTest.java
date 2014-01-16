package org.overture.typechecker.tests.framework;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.test.framework.results.Result;
import org.overture.typechecker.tests.utils.OvertureTestHelper;

public abstract class CommonTypeCheckerTest extends TypeCheckTestCase
{
	private Dialect dialect;

	public CommonTypeCheckerTest(Dialect dialect, File file, String suiteName,
			File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
		this.dialect = dialect;
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.dialect = dialect;
		Settings.release = Release.VDM_10;
	}

	@Test
	public void test() throws Exception
	{
		configureResultGeneration();
		try
		{
			Result<Boolean> result = null;

			switch (dialect)
			{
				case VDM_PP:
					result = new OvertureTestHelper().typeCheckPp(file);
					break;
				case VDM_RT:
					result = new OvertureTestHelper().typeCheckRt(file);
					break;
				case VDM_SL:
					result = new OvertureTestHelper().typeCheckSl(file);
					break;

			}

			compareResults(result, file.getName() + ".result");
		} finally
		{
			unconfigureResultGeneration();
		}
	}

	protected File getStorageLocation()
	{
		return file.getParentFile();
	}

	protected File getInputLocation()
	{
		return file.getParentFile();
	}

	@Override
	protected File createResultFile(String filename)
	{
		getStorageLocation().mkdirs();
		return getResultFile(filename);
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(getStorageLocation(), filename);
	}

}
