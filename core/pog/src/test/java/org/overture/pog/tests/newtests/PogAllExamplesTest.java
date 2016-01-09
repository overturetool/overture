package org.overture.pog.tests.newtests;

import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.core.tests.examples.ParamExamplesTest;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;

import com.google.gson.reflect.TypeToken;

import junitparams.JUnitParamsRunner;

/**
 * Working examples of all examples tests for the pog
 * 
 * @author ldc
 */
@RunWith(JUnitParamsRunner.class)
public class PogAllExamplesTest extends ParamExamplesTest<Boolean>
{
	@Override
	public Boolean processModel(List<INode> model)
	{
		IProofObligationList ipol;
		try
		{
			ipol = ProofObligationGenerator.generateProofObligations(model);
			Assert.assertNotNull(ipol);
			
			return true;
		} catch (AnalysisException e)
		{
			fail("Could not process model in test " + testName);
			e.printStackTrace();
		}
		return false;

	}

	@Override
	public void compareResults(Boolean actual, Boolean expected)
	{
		Assert.assertEquals(expected, actual);
	}

	@Override
	protected String getUpdatePropertyString()
	{
		return "tests.update.pog.allexamples";

	}

	@Override
	public Type getResultType()
	{
		Type resultType = new TypeToken<Boolean>()
		{
		}.getType();
		return resultType;
	}

	private static String EXAMPLES_ROOT = "../../externals/docrepo/examples/";
	
	@Override
	protected String getRelativeExamplesPath()
	{
		return EXAMPLES_ROOT;
	}
	
	@Override
	protected List<String> getExamplesToSkip()
	{
		LinkedList<String> toSkip = new LinkedList<String>();
		toSkip.add("AutomatedStockBrokerPP");
		
		return toSkip;
	}

}
