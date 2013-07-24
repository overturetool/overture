package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.typechecker.assistant.definition.ALocalDefinitionAssistantTC;

public class ALocalDefinitionAssistantInterpreter extends
		ALocalDefinitionAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALocalDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static NameValuePairList getNamedValues(ALocalDefinition d,
			Context initialContext)
	{
		NameValuePair nvp = new NameValuePair(d.getName(), initialContext.lookup(d.getName()));
		return new NameValuePairList(nvp);
	}

}
