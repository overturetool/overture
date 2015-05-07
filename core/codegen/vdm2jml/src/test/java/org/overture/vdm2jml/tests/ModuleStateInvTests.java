package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.vdm2jml.tests.util.TestDataCollector;

public class ModuleStateInvTests extends StateTestBase
{
	@BeforeClass
	public static void init() throws AnalysisException,
			UnsupportedModelingException
	{
		StateTestBase.init("ModuleStateInv.vdmsl");
	}

	public static void checkAssertion(String methodName, boolean expectAssignment,
			boolean expectMetaData)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		AMethodDeclCG assignSt = getMethod(genModule.getMethods(), methodName);

		Assert.assertTrue("Could not find method '" + methodName + "'", assignSt != null);

		TestDataCollector dataCollector = new TestDataCollector();
		assignSt.apply(dataCollector);

		if (expectAssignment)
		{
			Assert.assertTrue("Expected to find a single assignment in the '"
					+ methodName + "' method", dataCollector.getAssignments().size() == 1);
			Assert.assertTrue("Expected to find no map seq updates in the '"
					+ methodName + "' method", dataCollector.getMapSeqUpdates().isEmpty());
		} else
		{
			Assert.assertTrue("Expected to find no assignments in the '"
					+ methodName + "' method", dataCollector.getAssignments().isEmpty());
			Assert.assertTrue("Expected to find a single map seq update in the '"
					+ methodName + "' method", dataCollector.getMapSeqUpdates().size() == 1);
		}

		if (expectMetaData)
		{
			Assert.assertTrue("Expected to find a single assertion in the '"
					+ methodName + "' method", dataCollector.getAssertions().size() == 1);

			List<? extends ClonableString> metaData = dataCollector.getAssertions().get(0).getMetaData();

			Assert.assertTrue("Expected a single assertion annotation", metaData.size() == 1);

			String assertStr = metaData.get(0).value;

			Assert.assertEquals("Got unexpected assertion in method '"
					+ methodName + "'", "//@ assert inv_St(St);", assertStr);
		} else
		{
			Assert.assertTrue("Expected no assertions in the '" + methodName
					+ "' method", dataCollector.getAssertions().isEmpty());
		}
	}

	@Test
	public void updateEntireState()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignSt", true, true);
	}

	@Test
	public void updateEntireStateAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignSt", true, false);
	}

	@Test
	public void updateField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignX", true, true);
	}

	@Test
	public void updateFieldAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignX", true, false);
	}

	@Test
	public void updateSeqElem()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignS", false, true);
	}

	@Test
	public void updateSeqElemAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignS", false, false);
	}

	@Test
	public void updateMapRng()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignM", false, true);
	}

	@Test
	public void updateMapRngAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignM", false, false);
	}
}
