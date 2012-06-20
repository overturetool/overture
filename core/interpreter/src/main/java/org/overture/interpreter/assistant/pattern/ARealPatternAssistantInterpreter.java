package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.ARealPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.ARealPatternAssistantTC;

public class ARealPatternAssistantInterpreter extends ARealPatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(ARealPattern p,
			Value expval, Context ctxt) throws PatternMatchException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (expval.realValue(ctxt) != p.getValue().value)
			{
				RuntimeError.patternFail(4113, "Real pattern match failed",p.getLocation());
			}
		}
		catch (ValueException e)
		{
			RuntimeError.patternFail(e,p.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

}
