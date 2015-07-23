package org.overture.vdm2jml.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

public class StateTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("State.vdmsl");
	}
	
	@Before
	public void prepareTest()
	{
		validateGenModuleAndStateType();
	}
	
	@Test
	public void testModuleStateIsSpecPublic()
	{
		Assert.assertTrue("Expected a single field to represent the state", genModule.getFields().size() == 1);

		AFieldDeclCG stateField = genModule.getFields().getFirst();

		Assert.assertTrue("Expected only a single JML annotation for the state field", stateField.getMetaData().size() == 1);

		String annotation = stateField.getMetaData().get(0).value;

		Assert.assertEquals("Expected state field to be @spec_public", SPEC_PUBLIC_ANNOTATION, annotation);
	}
	
	@Test
	public void testGenStateTypeMethodsArePure()
	{
		Assert.assertTrue("Expected seven methods in the state type ", genStateType.getMethods().size() == 7);

		for (AMethodDeclCG m : genStateType.getMethods())
		{
			if (m.getName().equals("hashCode") || m.getName().equals("equals")
					|| m.getName().equals("toString")
					|| m.getName().equals("copy"))
			{
				assertPureMethod(m);
			} else
			{
				assertNotPureMethod(m);
			}
		}
	}
	
	@Test
	public void testModuleHasNoInvFunction()
	{
		// The state invariant constrains the type of the state
		// see https://github.com/overturetool/overture/issues/459
		Assert.assertTrue("The state invariant constrains the type of the state", genModule.getInvariant() == null);
	}
	
	@Test
	public void testInv()
	{
		Assert.assertTrue("Expected only a single ghost variable declaration to exist", genModule.getMetaData().size() == 1);
		
		String expected = "/*@ public ghost static boolean invChecksOn = true; @*/";
		
		Assert.assertEquals("Got unexpected invariant", expected, genModule.getMetaData().get(0).value);
	}
}
