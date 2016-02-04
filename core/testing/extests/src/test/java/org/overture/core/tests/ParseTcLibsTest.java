package org.overture.core.tests;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.core.tests.examples.ExampleAstData;
import org.overture.core.tests.examples.ExampleSourceData;
import org.overture.core.tests.examples.ExamplesUtility;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

/**
 * Basic test for all the libs. Simply ensures that they all parse and type-check correctly. <br>
 * 
 * @author ldc
 */

@RunWith(Parameterized.class)
public class ParseTcLibsTest
{
	ExampleSourceData testData;

	private static String LIBS_ROOT = "../../../externals/docrepo/examples/libs/";
	
	public ParseTcLibsTest(String _, ExampleSourceData testData)
	{
		this.testData = testData;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws IOException, URISyntaxException
	{
		Collection<Object[]> r = new Vector<Object[]>();

		Collection<ExampleSourceData> examples = ExamplesUtility.getLibSources(LIBS_ROOT);

		for (ExampleSourceData e : examples)
		{
			r.add(new Object[] { e.getName(), e });
		}

		return r;
	}

	@Test
	public void testParseTc() throws IOException, ParserException, LexException
	{
		ExampleAstData ex = ExamplesUtility.parseTcExample(testData);
		assertNotNull("Could not Parse/TC " + ex.getExampleName());
	}
}
