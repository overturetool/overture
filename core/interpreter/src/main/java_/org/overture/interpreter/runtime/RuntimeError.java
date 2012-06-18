package org.overture.interpreter.runtime;

import org.overture.ast.lex.LexLocation;
import org.overture.interpreter.values.Value;
import org.overture.parser.messages.LocatedException;


public class RuntimeError
{

	public static void abort(LexLocation location, int number, String msg, Context ctxt)
	{
		throw new ContextException(number, msg, location, ctxt);
	}

	public static Value abort(LexLocation location, ValueException ve)
	{
		throw new ContextException(ve, location);
	}

	/**
	 * Abort the runtime interpretation of the definition, throwing a
	 * ContextException to indicate the call stack. The information is
	 * based on that in the Exception passed.
	 *
	 * @param e		The Exception that caused the problem.
	 * @param ctxt	The runtime context that caught the exception.
	 */

	public static Value abort(LocatedException e, Context ctxt)
	{
		throw new ContextException(e.number, e.getMessage(), e.location, ctxt);
	}
		
	
	
}
