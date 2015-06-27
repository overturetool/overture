package org.overture.vdm2jml.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

/**
 * This test is like the  PreCondNoStateTests test except that it
 * produces a different requires annotation for the module operation
 * since the test input module has state
 * 
 * @author pvj
 *
 */
public class PreCondWithStateTests extends PreCondNoStateTests
{
	@BeforeClass
	public static void init() throws AnalysisException
	{
		AnnotationTestsBase.init("PreCondWithState.vdmsl");
	}
	
	@Test
	@Override
	public void testOpRequiresAnnotation()
	{
		AMethodDeclCG op = getMethod(genModule.getMethods(), "op");
		
		// The generated module has a state component, which must be passed to the
		// pre condition function
		Assert.assertEquals("Got unexpected requires annotation for operation 'op'",
				"//@ requires pre_op(a,St);",
				getAnnotation(op, 0));
	}
}
