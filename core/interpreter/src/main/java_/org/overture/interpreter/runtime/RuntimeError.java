package org.overture.interpreter.runtime;

import org.overture.ast.lex.LexLocation;


public class RuntimeError
{

	public static void abort(LexLocation location, int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}

	public static void abort(LexLocation location, ValueException ve)
	{
		throw new ContextException(ve, location);
	}
	
}
