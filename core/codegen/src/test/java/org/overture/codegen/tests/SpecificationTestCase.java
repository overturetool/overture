package org.overture.codegen.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.constants.IText;
import org.overture.codegen.utils.GeneratedClass;
import org.overture.codegen.vdmcodegen.CodeGenUtil;

public class SpecificationTestCase extends CodeGenBaseTestCase
{

	public SpecificationTestCase()
	{
		super();
	}

	public SpecificationTestCase(File file)
	{
		super(file);
	}

	@Override
	protected String generateActualOutput() throws AnalysisException
	{	
		StringBuilder generatedCode = new StringBuilder();
		List<GeneratedClass> classes = CodeGenUtil.generateOO(file);
		
		for (GeneratedClass classCg : classes)
		{
			generatedCode.append(classCg.getContent());
			generatedCode.append(IText.NEW_LINE);
		}
		
		int lastIndex = generatedCode.lastIndexOf(IText.NEW_LINE);
		
		if(lastIndex >= 0)
			generatedCode.replace(lastIndex, lastIndex + IText.NEW_LINE.length(), "");
		
		return generatedCode.toString();
	}

	@Override
	protected String getTestOverview(String input, String expectedResult, String actualResult)
	{
		return getName();
	}
	
}
