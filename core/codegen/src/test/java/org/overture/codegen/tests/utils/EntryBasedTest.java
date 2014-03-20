package org.overture.codegen.tests.utils;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

public abstract class EntryBasedTest extends TestHandler
{
	protected static final String ENTRY_CLASS_NAME = "Entry";
	protected static final String ENTRY_METHOD_CALL = "Run()";
	protected static final String JAVA_ENTRY_CALL = ENTRY_CLASS_NAME + "." + ENTRY_METHOD_CALL;
	protected static final String VDM_ENTRY_CALL = ENTRY_CLASS_NAME + "`" + ENTRY_METHOD_CALL;

	@Override
	public Value interpretVdm(File intputFile) throws Exception
	{
		return InterpreterUtil.interpret(Dialect.VDM_RT, VDM_ENTRY_CALL, intputFile);
	}
}