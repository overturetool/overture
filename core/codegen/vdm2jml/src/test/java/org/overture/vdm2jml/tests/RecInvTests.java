package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;

public class RecInvTests extends AnnotationTestsBase
{
	private static final String REC_NAME = "Rec";

	private static AClassDeclCG recTypeDef;

	@BeforeClass
	public static void init() throws AnalysisException
	{
		List<AClassDeclCG> classes = AnnotationTestsBase.getClasses("RecInv.vdmsl");

		for (AClassDeclCG clazz : classes)
		{
			if (clazz.getName().equals(REC_NAME))
			{
				recTypeDef = clazz;
			}
		}
	}
	
	@Test
	public void recMethodsPure()
	{
		AnnotationTestsBase.assertRecMethodsPurity(recTypeDef.getMethods());
	}

	@Test
	public void recInv()
	{
		Assert.assertTrue("Expected a record type definition in the generated module", recTypeDef != null);

		Assert.assertEquals("Got unexpected record type definition invariant",
				"//@ public instance invariant project.Entry.invChecksOn ==> inv_Rec(x);", AnnotationTestsBase.getLastAnnotation(recTypeDef));
	}
	
	@Test
	public void invFuncIsPure()
	{
		Assert.assertTrue("Expected the record type definition invariant to be a method declaration at this point",
				recTypeDef.getInvariant() instanceof AMethodDeclCG);

		AnnotationTestsBase.assertPureMethod((AMethodDeclCG) recTypeDef.getInvariant());
	}
	
	@Test
	public void invFuncIsHelper()
	{
		AnnotationTestsBase.assertHelper(recTypeDef.getInvariant(),
				"Expected record type definition invariant function to be a helper");
	}
}
