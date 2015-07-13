package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

public class PurityTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("FuncsOpsOnly.vdmsl");
	}

	@Before
	public void prepareTest()
	{
		validGeneratedModule();
	}

	@Test
	public void testNoModuleStateAnnotations()
	{
		Assert.assertTrue("Expected no module state annotations", genModule.getFields().isEmpty());
	}

	@Test
	public void testGenModuleFuncsArePure()
	{
		assertPure(getGenFunctions(genModule.getMethods()));
	}

	@Test
	public void operationsNotAnnotated()
	{
		List<AMethodDeclCG> genOps = getGenMethods(genModule.getMethods());

		Assert.assertTrue("Expected the generated module to have operations", !genOps.isEmpty());

		for (AMethodDeclCG op : genOps)
		{
			Assert.assertTrue("Expected operations for this generated module to have no annotations", op.getMetaData().isEmpty());
		}
	}

	@Test
	public void testNoStateInvInGenModule()
	{
		Assert.assertTrue("Expected no state annotations", genModule.getMetaData().isEmpty());
	}
}
