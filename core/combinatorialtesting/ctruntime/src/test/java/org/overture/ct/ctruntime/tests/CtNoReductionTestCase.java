package org.overture.ct.ctruntime.tests;

import java.io.File;

public class CtNoReductionTestCase extends CtTestCaseBase
{
	public CtNoReductionTestCase()
	{
		super();
	}

	public CtNoReductionTestCase(File file)
	{
		super(file);
	}

	public String[] getArgs(String traceName, File traceFolder,
			File specFileWithExt)
	{
		return testHelper.buildArgs(traceName, PORT, traceFolder, specFileWithExt);
	}
}
