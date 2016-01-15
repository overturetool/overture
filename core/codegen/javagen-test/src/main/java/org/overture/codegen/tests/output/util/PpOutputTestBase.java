package org.overture.codegen.tests.output.util;

import org.junit.Before;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

abstract public class PpOutputTestBase extends SpecificationTestBase
{
	public PpOutputTestBase(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
	}

	abstract protected String getUpdatePropertyString();
}
