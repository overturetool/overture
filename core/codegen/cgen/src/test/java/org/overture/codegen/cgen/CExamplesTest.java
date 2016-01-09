package org.overture.codegen.cgen;

import java.io.File;

import org.junit.Test;

public class CExamplesTest
{
	static final String cexamplesBase = System.getProperty("cexamples.path");
	static final String outputFolder = new File("target/test-cgen/"+CExamplesTest.class.getSimpleName().replace('/', File.separatorChar)).getAbsolutePath();

	static String getPath(String rpath)
	{
		return new File(cexamplesBase, rpath.replace('/', File.separatorChar)).getAbsolutePath();
	}

	@Test
	public void a()
	{
		CGenMain.main(new String[] {"-dest",outputFolder, getPath("classes/A.vdmrt") });
	}
	
	@Test
	public void c()
	{
		CGenMain.main(new String[] {"-dest",outputFolder, getPath("classes/C.vdmrt") });
	}
	
	@Test
	public void Numeric()
	{
		CGenMain.main(new String[] {"-dest",outputFolder, getPath("expressions/ExpressionNumeric.vdmrt") });
	}
	
	@Test
	public void Seq()
	{
		CGenMain.main(new String[] {"-dest",outputFolder, getPath("expressions/ExpressionSeq.vdmrt") });
	}
}
