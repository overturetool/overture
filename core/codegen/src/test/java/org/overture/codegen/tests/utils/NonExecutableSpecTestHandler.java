package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NonExecutableSpecTestHandler extends TestHandler
{
	@Override
	public void writeGeneratedCode(File parent, File resultFile)
			throws IOException
	{
		List<StringBuffer> content = TestUtils.readJavaModulesFromResultFile(resultFile);

		if (content.size() == 0)
		{
			System.out.println("Got no clases for: " + resultFile.getName());
			return;
		}

		parent.mkdirs();

		for (StringBuffer classCgStr : content)
		{
			File tempFile = consTempFile(TestUtils.getJavaModuleName(classCgStr), parent, classCgStr);
			
			writeToFile(classCgStr.toString(), tempFile);
		}
	}
}
