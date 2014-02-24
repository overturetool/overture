package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.IOException;

import org.overture.ast.lex.Dialect;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

public class ComplexExpressionTestHandler extends TestHandler
{
	private static final String ENTRY_CLASS_NAME = "Entry";
	private static final String ENTRY_METHOD_CALL = "Run()";
	
	private static final String JAVA_ENTRY_CALL =  ENTRY_CLASS_NAME + "." + ENTRY_METHOD_CALL;
	private static final String VDM_ENTRY_CALL = ENTRY_CLASS_NAME + "`" + ENTRY_METHOD_CALL;
	
	
	@Override
	public void writeGeneratedCode(File parent, String generatedCode) throws IOException
	{
		injectArgIntoMainClassFile(parent, JAVA_ENTRY_CALL);
		
		File generatedCodeFile = getFile(parent, ENTRY_CLASS_NAME);
		
		writeToFile(generatedCode, generatedCodeFile);
	}

	@Override
	public Value interpretVdm(File intputFile) throws Exception
	{
		initVdmEnv();
		
		return InterpreterUtil.interpret(Dialect.VDM_RT, VDM_ENTRY_CALL, intputFile);
	}

}