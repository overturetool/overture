package org.overture.core.tests.example;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.overture.tools.examplepackager.util.ExampleTestData;
import org.overture.tools.examplepackager.util.ExampleTestUtils;

/**
 * Basic test for all the examples. Simply ensures that they all parse and type-check correctly. Examples with
 * purposeful errors are not tested at all. This test needs to be improved to also check examples with specific errors.
 * 
 * 
 * @author ldc
 */
public class ExampleModelsTest
{

	// FIXME make Example Models Tests test for models with errors as well.

	@Test
	public void testParseTcAllExamples() throws IOException
	{
		Collection<ExampleTestData> examples = ExampleTestUtils.getCorrectExamplesSources();

		for (ExampleTestData ex : examples)
		{
			assertNotNull("Could not Parse/TC " + ex.getName(), ex);
		}
	}

}
