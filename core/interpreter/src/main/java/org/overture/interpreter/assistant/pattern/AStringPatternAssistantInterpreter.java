package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.AStringPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.RuntimeError;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.pattern.AStringPatternAssistantTC;

public class AStringPatternAssistantInterpreter extends
		AStringPatternAssistantTC
{

	public static List<NameValuePairList> getAllNamedValues(AStringPattern p,
			Value expval, Context ctxt) throws PatternMatchException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (!expval.stringValue(ctxt).equals(p.getValue().value))
			{
				RuntimeError.patternFail(4122, "String pattern match failed",p.getLocation());
			}
		}
		catch (ValueException e)
		{
			RuntimeError.patternFail(e,p.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	public static int getLength(AStringPattern pattern)
	{
		return pattern.getValue().value.length();
	}

}
