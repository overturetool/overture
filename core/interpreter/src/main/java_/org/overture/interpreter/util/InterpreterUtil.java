package org.overture.interpreter.util;

import org.overture.interpreter.runtime.ClassInterpreter;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ModuleInterpreter;
import org.overture.interpreter.values.Value;

public class InterpreterUtil
{
	public static Interpreter getInterpreter(ModuleListInterpreter modules)
			throws Exception
	{
		return new ModuleInterpreter(modules);
	}

	public static Interpreter getInterpreter(ClassListInterpreter classes)
			throws Exception
	{
		return new ClassInterpreter(classes);
	}
	
	public static Value interpret(String content) throws Exception
	{
		Interpreter interpreter =getInterpreter(new ModuleListInterpreter());
		interpreter.init(null);
		Value val = interpreter.execute(content, null);
		return val;
	}
}
