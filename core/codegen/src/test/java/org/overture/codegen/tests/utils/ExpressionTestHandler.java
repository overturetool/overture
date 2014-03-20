package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;

import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Release;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

class ExpressionTestHandler extends ExecutableTestHandler
{
	public ExpressionTestHandler(Release release)
	{
		super(release);
	}

	public void writeGeneratedCode(File parent, File resultFile) throws IOException
	{
		String generatedExpression = readFromFile(resultFile);
		injectArgIntoMainClassFile(parent, generatedExpression);
	}

	@Override
	public Value interpretVdm(File intputFile) throws Exception
	{
		initVdmEnv();
		
		String input = GeneralUtils.readFromFile(intputFile);

		return InterpreterUtil.interpret(input);
	}
}
