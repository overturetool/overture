package org.overture.pog.tests.framework;

import java.io.File;
import java.io.IOException;

import org.overture.pog.obligations.ProofObligationList;
import org.overture.pog.util.PogUtil;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.syntax.ParserException;

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

	public void test() throws ParserException, LexException, IOException
	{
		Result<ProofObligationList> result;
		try
		{
			result = convert(PogUtil.pogSl(file));
			compareResults(result, file.getAbsolutePath());
		} catch (Exception e)
		{
			assert false : "Test failed due to parse or type check error";
		}
	}

}
