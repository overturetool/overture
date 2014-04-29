package org.overture.interpreter.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.patterns.ACharacterPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.PatternMatchException;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;

public class ACharacterPatternAssistantInterpreter

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACharacterPatternAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		//super();
		this.af = af;
	}

	public static List<NameValuePairList> getAllNamedValues(
			ACharacterPattern p, Value expval, Context ctxt)
			throws PatternMatchException
	{
		List<NameValuePairList> result = new Vector<NameValuePairList>();

		try
		{
			if (expval.charValue(ctxt) != p.getValue().getValue())
			{
				VdmRuntimeError.patternFail(4107, "Character pattern match failed", p.getLocation());
			}
		} catch (ValueException e)
		{
			VdmRuntimeError.patternFail(e, p.getLocation());
		}

		result.add(new NameValuePairList());
		return result;
	}

}
