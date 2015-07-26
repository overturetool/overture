package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.vdm2jml.tests.util.TestDataCollector;

public class ModuleStateInvTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("ModuleStateInv.vdmsl");
	}

	public static void checkAssertion(String methodName, Update update,
			boolean expectMetaData)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		AMethodDeclCG assignSt = getMethod(genModule.getMethods(), methodName);

		Assert.assertTrue("Could not find method '" + methodName + "'", assignSt != null);

		TestDataCollector dataCollector = new TestDataCollector();
		assignSt.apply(dataCollector);

		if (update == Update.ASSIGN)
		{
			Assert.assertTrue("Expected to find a single assignment in the '"
					+ methodName + "' method", dataCollector.getAssignments().size() == 1);
			Assert.assertTrue("Expected to find no map seq updates in the '"
					+ methodName + "' method", dataCollector.getMapSeqUpdates().isEmpty());
			Assert.assertTrue("Expected to find no set calls in the '"
					+ methodName + "' method", dataCollector.getSetCalls().isEmpty());
			
		} else if(update == Update.MAP_SEQ_UPDATE)
		{
			Assert.assertTrue("Expected to find no assignments in the '"
					+ methodName + "' method", dataCollector.getAssignments().isEmpty());
			Assert.assertTrue("Expected to find a single map seq update in the '"
					+ methodName + "' method", dataCollector.getMapSeqUpdates().size() == 1);
			Assert.assertTrue("Expected to find no set calls in the '"
					+ methodName + "' method", dataCollector.getSetCalls().isEmpty());
		}
		else
		{
			//Setter call
			Assert.assertTrue("Expected to find no assignments in the '"
					+ methodName + "' method", dataCollector.getAssignments().isEmpty());
			Assert.assertTrue("Expected to find no map seq updates in the '"
					+ methodName + "' method", dataCollector.getMapSeqUpdates().isEmpty());
			Assert.assertTrue("Expected to find a single set call in the '"
					+ methodName + "' method", dataCollector.getSetCalls().size() == 1);
		}

		if (expectMetaData)
		{
			Assert.assertTrue("Expected to find a single assertion in the '"
					+ methodName + "' method", dataCollector.getAssertions().size() == 1);

			List<? extends ClonableString> metaData = dataCollector.getAssertions().get(0).getMetaData();

			Assert.assertTrue("Expected a single assertion annotation", metaData.size() == 1);

			String assertStr = metaData.get(0).value;

			Assert.assertEquals("Got unexpected assertion in method '"
					+ methodName + "'", "//@ assert St.valid();", assertStr);
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
		checkAssertion("assignSt", Update.ASSIGN, false);
	}

	@Test
	public void updateEntireStateAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		// No need to assert since the violation will be picked up by the
		// atomicTmp var assignment
		checkAssertion("atomicAssignSt", Update.ASSIGN, false);
	}

	@Test
	public void updateField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignX", Update.SET_CALL, false);
	}

	@Test
	public void updateFieldAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignX", Update.SET_CALL, true);
	}

	@Test
	public void updateSeqElem()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignS", Update.MAP_SEQ_UPDATE, true);
	}

	@Test
	public void updateSeqElemAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignS", Update.MAP_SEQ_UPDATE, true);
	}

	@Test
	public void updateMapRng()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("assignM", Update.MAP_SEQ_UPDATE, true);
	}

	@Test
	public void updateMapRngAtomic()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		checkAssertion("atomicAssignM", Update.MAP_SEQ_UPDATE, true);
	}
}
