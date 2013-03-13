package org.overture.typechecker.tests.external;

import java.io.File;

import org.overture.test.framework.TestResourcesResultTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class NewExternalTypeCheckTestCase extends
		TestResourcesResultTestCase<Boolean> {
	public NewExternalTypeCheckTestCase()
	{
		super();
	}

	public NewExternalTypeCheckTestCase(File file)
	{
		super(file);
	}

	public NewExternalTypeCheckTestCase(File rootSource,String name, String content)
	{
		super(rootSource,name, content);
	}

	public NewExternalTypeCheckTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file,suiteName,testSuiteRoot);
	}

	public void encondeResult(Boolean result, Document doc,
			Element resultElement) {
		
	}

	public Boolean decodeResult(Node node) {
		return null;
	}

	@Override
	protected boolean assertEqualResults(Boolean expected, Boolean actual) {
		return true;
	}
	

}
