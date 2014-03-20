package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.constants.IText;
import org.overture.config.Release;

public class ExecutableSpecTestHandler extends EntryBasedTestHandler
{
	public ExecutableSpecTestHandler(Release release)
	{
		super(release);
	}

	@Override
	public void writeGeneratedCode(File parent, File resultFile) throws IOException
	{
		injectArgIntoMainClassFile(parent, JAVA_ENTRY_CALL);
		
		List<StringBuffer> content = TestUtils.readJavaModulesFromResultFile(resultFile);

		if (content.size() == 0)
		{
			System.out.println("Got no clases for: " + resultFile.getName());
			return;
		}

		parent.mkdirs();

		for (StringBuffer classCgStr : content)
		{
			String className = TestUtils.getJavaModuleName(classCgStr);
			File tempFile = consTempFile(className, parent, classCgStr);
			
			String output;
			
			if(!className.equals(IOoAstConstants.QUOTES_INTERFACE_NAME))
			{
				output = classCgStr.toString().replaceFirst(className, className + " implements Serializable");
				output = output.replaceFirst("public", "import java.io.*;" + IText.NEW_LINE + IText.NEW_LINE + " public");
				
			}
			else
			{
				output = classCgStr.toString();
			}
			
			writeToFile(output, tempFile);
		}		
	}
}
