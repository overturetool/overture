package org.overture.pog.tests.newtests;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.AllExamplesHelper;
import org.overture.core.tests.AllExamplesHelper.ExampleAstData;
import org.overture.core.tests.ParamExamplesTest;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
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

	public PogAllExamplesTest(String _, List<INode> model, String result)
	{
		super(_, model, result);
		// uncomment line below to update results
		// updateResult = true;
	}

	private static String EXAMPLES_RESULTS_ROOT = "src/test/resources/exampleResults/";

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData() throws ParserException,
			LexException
	{
		Collection<ExampleAstData> examples = AllExamplesHelper.getExamplesAsts();
		Collection<Object[]> r = new Vector<Object[]>();

		for (ExampleAstData e : examples)
		{
			r.add(new Object[] { e.getExampleName(), e.getModel(),
					EXAMPLES_RESULTS_ROOT + e.getExampleName() });
		}

		return r;
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
			fail("Could not process model.");
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void compareResults(PogTestResult actual, PogTestResult expected)
	{
		PogTestResult.compare(actual, expected);

	}

}
