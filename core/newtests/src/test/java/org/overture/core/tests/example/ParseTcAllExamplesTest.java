package org.overture.core.tests.example;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.core.tests.ParseTcFacade;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.tools.examplepackager.util.ExampleTestData;
import org.overture.tools.examplepackager.util.ExampleTestUtils;

/**
 * Basic test for all the examples. Simply ensures that they all parse and type-check correctly. Examples with
 * purposeful errors are not tested at all. This test needs to be improved to also check examples with expected errors.
 * 
 * @author ldc
 */

@RunWith(Parameterized.class)
public class ExampleModelsTest
{
	ExampleTestData testData;

	public ExampleModelsTest(String _, ExampleTestData testData)
	{
		this.testData = testData;
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws IOException
	{
		Collection<Object[]> r = new Vector<Object[]>();

		Collection<ExampleTestData> examples = ExampleTestUtils.getCorrectExamplesSources();

		for (ExampleTestData e : examples)
		{
			r.add(new Object[] { e.getName(), e });
		}

		return r;
	}

	@Test
	public void testParseTc() throws IOException, ParserException, LexException
	{
		ExampleAstData ex = ParseTcFacade.parseExample(testData);
		assertNotNull("Could not Parse/TC " + ex.getExampleName());
	}

}
