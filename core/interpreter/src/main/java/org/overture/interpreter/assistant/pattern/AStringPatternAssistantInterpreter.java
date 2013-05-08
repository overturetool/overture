package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.AStringPattern;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.VdmRuntimeError;
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
			if (!expval.stringValue(ctxt).equals(p.getValue().getValue()))
			{
				VdmRuntimeError.patternFail(4122, "String pattern match failed",p.getLocation());
			}
		}
		catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e,p.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

	public static int getLength(AStringPattern pattern)
	{
		return pattern.getValue().getValue().length();
	}

}
