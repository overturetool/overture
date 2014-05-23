package org.overture.codegen.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.analysis.violations.InvalidNamesException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class SpecificationTestCase extends CodeGenBaseTestCase
{
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String MODULE_DELIMITER = LINE_SEPARATOR + "##########" + LINE_SEPARATOR;

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
		
		List<File> files = new LinkedList<File>();
		files.add(file);
		
		try
		{
			data = JavaCodeGenUtil.generateJavaFromFiles(files);
		} catch (InvalidNamesException e)
		{
			return JavaCodeGenUtil.constructNameViolationsString(e);
		} catch (UnsupportedModelingException e)
		{
			return JavaCodeGenUtil.constructUnsupportedModelingString(e);
		}
		
		List<GeneratedModule> classes = data.getClasses();
		
		for (GeneratedModule classCg : classes)
		{
			generatedCode.append(classCg.getContent());
			generatedCode.append(MODULE_DELIMITER);
		}
			
		GeneratedModule quoteData = data.getQuoteValues();
		
		if(quoteData != null)
		{
			generatedCode.append(LINE_SEPARATOR + quoteData.getContent());
			generatedCode.append(MODULE_DELIMITER);
		}
				
		return generatedCode.toString();
	}

	@Override
	protected String getTestOverview(String input, String expectedResult, String actualResult)
	{
		return getName();
	}
}
