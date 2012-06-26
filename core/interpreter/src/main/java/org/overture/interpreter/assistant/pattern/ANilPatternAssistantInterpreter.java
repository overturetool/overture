package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.ANilPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.ANilPatternAssistantTC;

public class ANilPatternAssistantInterpreter extends ANilPatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(ANilPattern p,
			Value expval, Context ctxt) throws PatternMatchException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		if (!(expval.deref() instanceof NilValue))
		{
			VdmRuntimeError.patternFail(4106, "Nil pattern match failed",p.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

}
