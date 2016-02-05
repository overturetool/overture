package org.overture.pog.tests.newtests;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.NoResultFileTest;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

@RunWith(Parameterized.class)
public abstract class AbsIntegrityTest extends NoResultFileTest<Boolean>
{

	protected final IntegrityCheck icVisitor;

	public AbsIntegrityTest(String nameParameter, String testParameter,
			IntegrityCheck icVisitor)
	{
		super(nameParameter, testParameter);
		this.icVisitor = icVisitor;
	}

	@Override
	public Boolean processModel(List<INode> arg0)
	{
		try
		{
			IProofObligationList ipol = ProofObligationGenerator.generateProofObligations(arg0);

			for (IProofObligation po : ipol)
			{
				po.getValueTree().getPredicate().apply(icVisitor);
				if (!icVisitor.getProblemNodes().isEmpty())
				{
					fail("Integrity error in" + po.toString()
							+ "\n Problem Nodes: \n"
							+ icVisitor.getProblemNodes().toString());
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
