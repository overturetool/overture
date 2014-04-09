package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.AIgnorePattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;

public class AIgnorePatternAssistantInterpreter

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIgnorePatternAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public static List<NameValuePairList> getAllNamedValues(AIgnorePattern p,
			Value expval, Context ctxt)
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();
		result.add(new NameValuePairList());
		return result;
	}

//	public static boolean isConstrained(AIgnorePattern pattern)
//	{
//		return false;
//	}

//	public static int getLength(AIgnorePattern pattern)
//	{
//		return PPatternAssistantInterpreter.ANY; // Special value meaning "any length"
//	}

}
