package org.overture.interpreter.assistant.pattern;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class AQuotePatternAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AQuotePatternAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	// public List<NameValuePairList> getAllNamedValues(AQuotePattern p,
	// Value expval, Context ctxt) throws PatternMatchException
	// {
	// List<NameValuePairList> result = new Vector<NameValuePairList>();
	//
	// try
	// {
	// if (!expval.quoteValue(ctxt).equals(p.getValue().getValue()))
	// {
	// VdmRuntimeError.patternFail(4112, "Quote pattern match failed", p.getLocation());
	// }
	// } catch (ValueException e)
	// {
	// VdmRuntimeError.patternFail(e, p.getLocation());
	// }
	//
	// result.add(new NameValuePairList());
	// return result;
	// }

}
