package org.overture.codegen.tests;

import java.io.File;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class ExpressionTestCase extends CodeGenBaseTestCase
{
	
	public ExpressionTestCase()
	{
		super();
	}

	public ExpressionTestCase(File file)
	{
		super(file);
	}

	@Override
	protected String generateActualOutput() throws AnalysisException
	{
		return JavaCodeGenUtil.generateJavaFromExp(CodeGenTestUtil.getFileContent(file));
	}

	@Override
	protected String getTestOverview(String input, String expectedResult, String actualResult)
	{
		return "Input:   \t" + input
		+ TemplateStructure.NEW_LINE + "Expected:\t"
		+ expectedResult + TemplateStructure.NEW_LINE
		+ "Actual:  \t" + actualResult;
	}
		
}
