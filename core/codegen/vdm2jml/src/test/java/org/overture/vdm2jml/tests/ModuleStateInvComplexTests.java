package org.overture.vdm2jml.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.vdm2jml.tests.util.Update;

public class ModuleStateInvComplexTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("ModuleStateInvComplex.vdmsl");
	}

	@Test
	public void seqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("seqField", Update.SET_CALL, 5);
	}

	@Test
	public void mapField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("mapField", Update.SET_CALL, 5);
	}

	@Test
	public void fieldSeqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("fieldSeqField", Update.SET_CALL, 7);
	}

	@Test
	public void seqSeqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("seqSeqField", Update.SET_CALL, 6);
	}
}
