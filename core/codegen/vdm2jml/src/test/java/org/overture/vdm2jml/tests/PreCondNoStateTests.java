package org.overture.vdm2jml.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

public class PreCondNoStateTests extends AnnotationTestsBase
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("PreCondNoState.vdmsl");
	}

	@Test
	public void testPreCondOfFuncIsPure()
	{
		assertFuncIsPureOnly("pre_f");
	}

	@Test
	public void testFuncRequiresAnnotation()
	{
		AMethodDeclCG func = getMethod(genModule.getMethods(), "f");
		
		Assert.assertEquals("Got unexpected requires annotation for function 'f'",
				"//@ requires pre_f(a,b);",
				getAnnotation(func, 0));
	}

	@Test
	public void testPreCondOfOpIsPure()
	{
		assertFuncIsPureOnly("pre_op");
	}

	@Test
	public void testOpRequiresAnnotation()
	{
		AMethodDeclCG op = getMethod(genModule.getMethods(), "op");
		
		// Note here that no state is expected to be passed
		// to the pre condition (the module has no state)
		Assert.assertEquals("Got unexpected requires annotation for operation 'op'",
				"//@ requires pre_op(a);",
				getAnnotation(op, 0));
	}
}
