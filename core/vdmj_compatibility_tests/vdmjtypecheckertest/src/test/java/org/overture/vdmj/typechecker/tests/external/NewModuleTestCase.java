package org.overture.vdmj.typechecker.tests.external;

import java.io.File;

import org.overture.vdmj.typechecker.tests.OvertureTestHelper;
import org.overturetool.test.framework.TestResourcesResultTestCase;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class NewModuleTestCase extends TestResourcesResultTestCase<Boolean>
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
		super(file, suiteName, testSuiteRoot);
	}

	@Override
	public void test() throws Exception
	{
		assertNotNull("File not set", file);
		compareResults(new OvertureTestHelper().typeCheckSl(file), file.getName() + ".result");

	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void encondeResult(Boolean result, Document doc,
			Element resultElement)
	{
		// TODO Auto-generated method stub
		
	}

	public Boolean decodeResult(Node node)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean assertEqualResults(Boolean expected, Boolean actual)
	{
		// TODO Auto-generated method stub
		return true;
	}

}
