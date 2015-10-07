package org.overture.pog.tests.newtests;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.NoResultFileTest;
import org.overture.core.tests.PathsProvider;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

@RunWith(Parameterized.class)
public class PogTypeIntegrityTest extends NoResultFileTest<Boolean>
{

	private static final String MICRO_INPUTS = "src/test/resources/micro";
	
	private TypeIntegrityVisitor tiVisitor = new TypeIntegrityVisitor();

	public PogTypeIntegrityTest(String nameParameter, String testParameter)
	{
		super(nameParameter, testParameter);
	}

	@Parameters(name = "{index} : {0}")
	public static Collection<Object[]> testData()
	{
		return PathsProvider.computePathsNoResultFiles(MICRO_INPUTS);
	}

	@Override
	public Boolean processModel(List<INode> arg0)
	{
		try
		{
			IProofObligationList ipol = ProofObligationGenerator.generateProofObligations(arg0);

			for (IProofObligation po : ipol)
			{
				po.getValueTree().getPredicate().apply(tiVisitor);
				if (!tiVisitor.getUntypeExps().isEmpty())
				{
					fail("Type integrity error in" + po.toString()
							+ "\n Untyped Exps: \n"
							+ tiVisitor.getUntypeExps().toString());
				}
			}

		} catch (AnalysisException e)
		{
			fail("Could not process " + testName);

		}
		return true;
	}

	@Override
	protected void processResult(Boolean actual)
	{
		// dont need to do anything
	}

}
