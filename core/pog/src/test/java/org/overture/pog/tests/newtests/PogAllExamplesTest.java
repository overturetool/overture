package org.overture.pog.tests.newtests;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.examples.ParamExamplesTest;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

/**
 * Working examples of all examples tests for the pog
 * 
 * @author ldc
 */
@RunWith(Parameterized.class)
public class PogAllExamplesTest extends ParamExamplesTest<PogTestResult>
{

	public PogAllExamplesTest(String name, List<INode> model, String result)
	{
		super(name, model, result);
	}



	@Override
	public PogTestResult processModel(List<INode> model)
	{
		IProofObligationList ipol;
		try
		{
			ipol = ProofObligationGenerator.generateProofObligations(model);
			PogTestResult actual = PogTestResult.convert(ipol);
			return actual;
		} catch (AnalysisException e)
		{
			fail("Could not process model in test " + testName);
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void compareResults(PogTestResult actual, PogTestResult expected)
	{
		PogTestResult.compare(actual, expected);
	}



	@Override
	protected String getUpdatePropertyString()
	{
		return "tests.update.pog.allexamples";

	}

}
