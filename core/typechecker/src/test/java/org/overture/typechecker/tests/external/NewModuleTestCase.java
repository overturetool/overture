package org.overture.typechecker.tests.external;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;

public class NewModuleTestCase extends NewExternalTypeCheckTestCase
{
	public NewModuleTestCase()
	{
		super();
	}

	public NewModuleTestCase(File file)
	{
		super(file);
	}

	public NewModuleTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	public NewModuleTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
	}

	@Override
	public void test() throws Exception
	{
		if(file==null)
		{
			return;
		}
		assertNotNull("File not set", file);
		compareResults(new OvertureTestHelper().typeCheckSl(file), file.getName() + ".result");
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}


}
