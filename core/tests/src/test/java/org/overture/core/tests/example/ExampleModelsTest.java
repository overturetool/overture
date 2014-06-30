package org.overture.core.tests.example;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;
import org.overture.core.tests.AllExamplesHelper;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.tools.examplepackager.util.ExampleTestData;
import org.overture.tools.examplepackager.util.ExampleTestUtils;

/**
 * Basic test for all the examples. Simply ensures that
 * they all parse and type-check correctly.
 * 
 * Examples with purposeful errors (for learning) are not
 * tested at all. This test needs to be improved to
 * check for the specific errors only.
 * @author ldc
 *
 */
public class ExampleModelsTest {
//FIXME make Example Models Tests test for models with errors as well.
	
	@Test
	public void testAllExamples() throws IOException{
		Collection<ExampleTestData> examples = ExampleTestUtils.getCorrectExamplesSources();
		
		for (ExampleTestData ex : examples){
			assertNotNull("Could not Parse/TC " + ex.getName(), ex);
		}
	}
	
}
