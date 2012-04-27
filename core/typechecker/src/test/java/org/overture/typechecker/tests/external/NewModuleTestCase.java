package org.overture.typechecker.tests.external;

import java.io.File;

import org.overture.typecheck.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;
import org.overturetool.test.framework.TestResourcesResultTestCase;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;

public class NewModuleTestCase extends TestResourcesResultTestCase
{
	public NewModuleTestCase()
	{
		super();
	}

	public NewModuleTestCase(File file)
	{
		super(file);
	}

	public NewModuleTestCase(String name, String content)
	{
		super(name, content);
	}

	public NewModuleTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
	}

	@Override
	public void test() throws Exception
	{
		assertNotNull("File not set", file);
		compareResults(new OvertureTestHelper().typeCheckSl(file), "typechecker.result");
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}


}
