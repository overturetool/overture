package org.overture.vdm2jml.tests;

import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;

public class ModuleStateInvComplexTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException,
			UnsupportedModelingException
	{
		AnnotationTestsBase.init("ModuleStateInvComplex.vdmsl");
	}

	@Test
	public void seqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("seqField", true, true);
	}

	@Test
	public void mapField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("mapField", true, true);
	}

	@Test
	public void fieldSeqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("fieldSeqField", true, true);
	}

	@Test
	public void seqSeqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("seqSeqField", true, true);
	}
}
