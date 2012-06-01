package org.overture.typechecker.tests.external;

import java.io.File;

import org.overture.typecheck.TypeChecker;
import org.overture.typechecker.tests.OvertureTestHelper;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;

public class NewClassRtTestCase extends NewExternalTypeCheckTestCase
{
	public NewClassRtTestCase()
	{
		super();
	}

	public NewClassRtTestCase(File file)
	{
		super(file);
	}

	public NewClassRtTestCase(String name, String content)
	{
		super(name, content);
	}

	public NewClassRtTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
	}

	@Override
	public void test() throws Exception
	{
		assertNotNull("File not set", file);
		compareResults(new OvertureTestHelper().typeCheckRt(file), file.getName() + ".result");

	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_RT;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}


}
