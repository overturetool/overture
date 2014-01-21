package org.overture.typechecker.tests.framework;

import java.io.File;
import java.io.PrintWriter;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.Properties;
import org.overture.test.framework.TestResourcesResultTestCase4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class TypeCheckTestCase extends
		TestResourcesResultTestCase4<Boolean>
{

	private static final String TESTS_TC_PROPERTY_PREFIX = "tests.tc.override.";

	public TypeCheckTestCase(File file, String suiteName, File testSuiteRoot)
	{
		super(file, suiteName, testSuiteRoot);
	}

	public void encondeResult(Boolean result, Document doc,
			Element resultElement)
	{

	}

	public Boolean decodeResult(Node node)
	{
		return null;
	}

	@Override
	protected boolean assertEqualResults(Boolean expected, Boolean actual,
			PrintWriter out)
	{
		// not used by type check
		return true;
	}

	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_TC_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_TC_PROPERTY_PREFIX
						+ getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}

	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}

	protected abstract String getPropertyId();
}
