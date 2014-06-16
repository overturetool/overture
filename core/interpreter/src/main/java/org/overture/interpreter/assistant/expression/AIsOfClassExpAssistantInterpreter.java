package org.overture.interpreter.assistant.expression;

import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.values.ObjectValue;

public class AIsOfClassExpAssistantInterpreter // extends AIsOfClassExpAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIsOfClassExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public boolean isOfClass(ObjectValue obj, String name)
	{
		if (obj.type.getName().getName().equals(name))
		{
			return true;
		} else
		{
			for (ObjectValue objval : obj.superobjects)
			{
				if (isOfClass(objval, name))
				{
					return true;
				}
			}
		}

		return false;
	}

}
