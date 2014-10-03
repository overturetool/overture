package org.overture.codegen.tests;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.JavaCodeGen;

public class IRTest
{	
	private JavaCodeGen javaCodeGen;
	
	public IRTest()
	{
		this.javaCodeGen = new JavaCodeGen();
	}
	
	@Test
	public void testVolatileField()
	{
		StringWriter writer = new StringWriter();
		
		AFieldDeclCG fieldDecl = new AFieldDeclCG();
		fieldDecl.setAccess("public");
		fieldDecl.setFinal(false);
		fieldDecl.setInitial(javaCodeGen.getInfo().getExpAssistant().consBoolLiteral(true));
		fieldDecl.setVolatile(true);
		fieldDecl.setStatic(true);
		fieldDecl.setName("flag");
		fieldDecl.setType(new ABoolBasicTypeCG());
		
		try
		{
			String expected = "public static volatile Boolean flag = true;";
			fieldDecl.apply(javaCodeGen.getJavaFormat().getMergeVisitor(), writer);
			String actual = GeneralUtils.removeDuplicateWhiteSpaces(writer.toString());
			Assert.assertTrue("Expected: " + expected + ". Got: " + actual, expected.equals(actual));
				
		} catch (AnalysisException e)
		{
			Assert.fail("Could not print field declaration");
		}
	}
}
