package org.overture.codegen.tests;

import java.io.File;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.constants.IText;
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
		String fileContent = CodeGenTestUtil.getFileContent(file);
		String generatedJava = JavaCodeGenUtil.generateJavaFromExp(fileContent).getContent().trim();
		
		return generatedJava;
	}

	@Override
	protected String getTestOverview(String input, String expectedResult, String actualResult)
	{
		return "Input:   \t" + input
		+ IText.NEW_LINE + "Expected:\t"
		+ expectedResult + IText.NEW_LINE
		+ "Actual:  \t" + actualResult;
	}
		
}
