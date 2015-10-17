package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.statements.AMetaStmCG;
import org.overture.vdm2jml.tests.util.TestDataCollector;
import org.overture.vdm2jml.tests.util.Update;

public class ModuleStateInvTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("ModuleStateInv.vdmsl");
	}

	public static void checkAssertion(String methodName, Update update,
			int noOfAsserts)
					throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		AMethodDeclCG assignSt = getMethod(genModule.getMethods(), methodName);

		Assert.assertTrue("Could not find method '" + methodName
				+ "'", assignSt != null);

		TestDataCollector dataCollector = new TestDataCollector();
		assignSt.apply(dataCollector);

		if (update == Update.ASSIGN)
		{
			Assert.assertTrue("Expected to find a single assignment in the '"
					+ methodName
					+ "' method", dataCollector.getAssignments().size() == 1);
			Assert.assertTrue("Expected to find no map seq updates in the '"
					+ methodName
					+ "' method", dataCollector.getMapSeqUpdates().isEmpty());
			Assert.assertTrue("Expected to find no set calls in the '"
					+ methodName
					+ "' method", dataCollector.getSetCalls().isEmpty());

		} else if (update == Update.MAP_SEQ_UPDATE)
		{
			Assert.assertTrue("Expected to find no assignments in the '"
					+ methodName
					+ "' method", dataCollector.getAssignments().isEmpty());
			Assert.assertTrue("Expected to find a single map seq update in the '"
					+ methodName
					+ "' method", dataCollector.getMapSeqUpdates().size() == 1);
			Assert.assertTrue("Expected to find no set calls in the '"
					+ methodName
					+ "' method", dataCollector.getSetCalls().isEmpty());
		} else
		{
			// Setter call
			Assert.assertTrue("Expected to find no assignments in the '"
					+ methodName
					+ "' method", dataCollector.getAssignments().isEmpty());
			Assert.assertTrue("Expected to find no map seq updates in the '"
					+ methodName
					+ "' method", dataCollector.getMapSeqUpdates().isEmpty());
			Assert.assertTrue("Expected to find a single set call in the '"
					+ methodName
					+ "' method", dataCollector.getSetCalls().size() == 1);
		}

		if (noOfAsserts > 0)
		{
			Assert.assertTrue("Expected to find " + noOfAsserts
					+ " assertion(s) in the '" + methodName
					+ "' method", dataCollector.getAssertions().size() == noOfAsserts);

			for (AMetaStmCG a : dataCollector.getAssertions())
			{
				List<? extends ClonableString> metaData = a.getMetaData();
				Assert.assertTrue("Expected only a single assertion", metaData.size() == 1);
				String assertStr = metaData.get(0).value;

				Assert.assertTrue("Got unexpected assertion in method '"
						+ methodName + "': " + assertStr, assertStr.contains(".valid()") || assertStr.contains("!= null") || assertStr.contains(".is_"));
			}
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
		checkAssertion("assignSt", Update.ASSIGN, 1);
	}

	@Test
	public void updateEntireStateAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		// No need to assert since the violation will be picked up by the
		// atomicTmp var assignment
		checkAssertion("atomicAssignSt", Update.ASSIGN, 2);
	}

	@Test
	public void updateField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignX", Update.SET_CALL, 1);
	}

	@Test
	public void updateFieldAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignX", Update.SET_CALL, 4);
	}

	@Test
	public void updateSeqElem()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignS", Update.MAP_SEQ_UPDATE, 5);
	}

	@Test
	public void updateSeqElemAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignS", Update.MAP_SEQ_UPDATE, 5);
	}

	@Test
	public void updateMapRng()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignM", Update.MAP_SEQ_UPDATE, 5);
	}

	@Test
	public void updateMapRngAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignM", Update.MAP_SEQ_UPDATE, 5);
	}
}
