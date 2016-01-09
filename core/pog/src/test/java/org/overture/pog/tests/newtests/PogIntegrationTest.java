package org.overture.pog.tests.newtests;

import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParamStandardTest;
import org.overture.core.tests.PathsProvider;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.pog.tests.newtests.PogTestResult.ResultComparison;

import com.google.gson.reflect.TypeToken;

@RunWith(Parameterized.class)
public class PogIntegrationTest extends ParamStandardTest<PogTestResult>
{

	private final static String INTEGRATION_ROOT = "src/test/resources/integration";
	

	private static final String UPDATE_PROPERTY = "tests.update.pog.legacy";

	public PogIntegrationTest(String nameParameter, String testParameter,
			String resultParameter)
	{
		super(nameParameter, testParameter, resultParameter);
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePaths(INTEGRATION_ROOT);
	}

	@Override
	public PogTestResult processModel(List<INode> ast)
	{
		try
		{
			IProofObligationList ipol = ProofObligationGenerator.generateProofObligations(ast);
			return PogTestResult.convert(ipol);
		} catch (AnalysisException e)
		{
			fail("Could not process test file " + testName);

		}
		return null;
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return UPDATE_PROPERTY;
	}

	@Override
	public void compareResults(PogTestResult actual, PogTestResult expected)
	{
		ResultComparison r = PogTestResult.compare(actual, expected);

		if (!r.isMatch())
		{
			fail(r.getMessage() + getTestResultUpdateMessage());
		}
	}

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<PogTestResult>()
		{
		}.getType();
		return resultType;
	}

}
