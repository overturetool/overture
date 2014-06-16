package org.overture.codegen.tests;

import java.io.File;
import java.io.IOException;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.utils.GeneralUtils;
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
		String fileContent;
		try
		{
			fileContent = GeneralUtils.readFromFile(file);
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
		String generatedJava = JavaCodeGenUtil.generateJavaFromExp(fileContent, getIrSettings(), getJavaSettings()).getContent().trim();
		String trimmed = generatedJava.replaceAll("\\s+", " ");
		
		return trimmed;
	}		
}
