package org.overture.codegen.tests;

import java.io.File;

import org.overture.ast.analysis.AnalysisException;
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
		String trimmed = generatedJava.replaceAll("\\s+", " ");
		
		return trimmed;
	}

	@Override
	protected String getTestOverview(String input, String expectedResult, String actualResult)
	{
		return "Input:   \t" + input
		+ "\nExpected:\t"
		+ expectedResult
		+ "\nActual:  \t" + actualResult;
	}
		
}
