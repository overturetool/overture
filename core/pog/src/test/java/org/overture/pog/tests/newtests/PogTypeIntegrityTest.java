package org.overture.pog.tests.newtests;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.core.tests.PathsProvider;

@RunWith(Parameterized.class)
public class PogTypeIntegrityTest extends AbsIntegrityTest
{

	private static final String MICRO_INPUTS = "src/test/resources/micro";
	private static final String INTEGRATION_INPUTS = "src/test/resources/integration/new";
	
	public PogTypeIntegrityTest(String nameParameter, String testParameter)
	{
		super(nameParameter, testParameter,new TypeIntegrityVisitor());
		
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePathsNoResultFiles(MICRO_INPUTS,INTEGRATION_INPUTS);
	}



}
