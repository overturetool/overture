package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;

public class ComplexExpressionTestHandler extends EntryBasedTest
{
	@Override
	public void writeGeneratedCode(File parent, File resultFile) throws IOException
	{
		injectArgIntoMainClassFile(parent, JAVA_ENTRY_CALL);

		String generatedCode = readFromFile(resultFile);
		File generatedCodeFile = getFile(parent, ENTRY_CLASS_NAME);
		
		writeToFile(generatedCode, generatedCodeFile);
	}

}