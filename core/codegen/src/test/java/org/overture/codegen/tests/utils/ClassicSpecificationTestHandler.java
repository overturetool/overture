package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.constants.IText;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.interpreter.values.Value;

public class ClassicSpecificationTestHandler extends EntryBasedTest
{
	@Override
	public Value interpretVdm(File intputFile) throws Exception
	{
		Settings.release = Release.CLASSIC;
		return super.interpretVdm(intputFile);
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
			File outputDir = parent;

			if (className.equals(IOoAstConstants.QUOTES_INTERFACE_NAME))
			{
				outputDir = new File(parent, IJavaCodeGenConstants.QUOTES_PACKAGE_NAME);
				outputDir.mkdirs();
			}

			File tempFile = new File(outputDir, className
					+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

			if (!tempFile.exists())
			{
				tempFile.createNewFile();
			}
			
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
