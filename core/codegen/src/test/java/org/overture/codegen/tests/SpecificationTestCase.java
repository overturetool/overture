package org.overture.codegen.tests;

import java.io.File;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.constants.IText;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.utils.InvalidNamesException;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

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
		
		GeneratedData data = null;
		
		try
		{
			data = JavaCodeGenUtil.generateJavaFromFile(file);
		} catch (InvalidNamesException e)
		{
			return JavaCodeGenUtil.constructNameViolationsString(e);
		}
		
		List<GeneratedModule> classes = data.getClasses();
		
		for (GeneratedModule classCg : classes)
		{
			generatedCode.append(classCg.getContent());
			generatedCode.append(IText.NEW_LINE);
		}
		
		int lastIndex = generatedCode.lastIndexOf(IText.NEW_LINE);
		
		if(lastIndex >= 0)
			generatedCode.replace(lastIndex, lastIndex + IText.NEW_LINE.length(), "");
		
		GeneratedModule quoteData = data.getQuoteValues();
		
		if(quoteData != null)
		{
			generatedCode.append(IText.NEW_LINE);
			generatedCode.append(quoteData.getContent());
		}
		
		lastIndex = generatedCode.lastIndexOf(IText.NEW_LINE);
		
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
