package org.overture.vdm2jml.tests;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

public class PostCondWithStateTests extends PostCondNoStateTests
{
	@BeforeClass
	public static void init() throws AnalysisException,
			UnsupportedModelingException
	{
		AnnotationTestsBase.init("PostCondWithState.vdmsl");
	}
	
	@Test
	@Override
	public void testOpNoResRequiresAnnotation()
	{
		AMethodDeclCG opNoRes = getMethod(genModule.getMethods(), "opNoRes");
		
		Assert.assertEquals("Got unexpected ensures annotation for operation 'opNoRes'",
				"//@ ensures post_opNoRes(\\old(St.copy()),St);",
				getAnnotation(opNoRes, 0));
	}
	
	@Test
	@Override
	public void testOpResRequresAnnotation()
	{
		AMethodDeclCG opRes = getMethod(genModule.getMethods(), "opRes");
		
		Assert.assertEquals("Got unexpected ensures annotation for operation 'opRes'",
				"//@ ensures post_opRes(a,\\result,\\old(St.copy()),St);",
				getAnnotation(opRes, 0));
	}
}
