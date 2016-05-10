package org.overture.interpreter.assistant.expression;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueMap;

import java.util.Map;

public class APostOpExpAssistantInterpreter implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public APostOpExpAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	public void populate(APostOpExp node, Context ctxt, String classname,
			ValueMap oldvalues) throws ValueException
	{
		for (Map.Entry<Value, Value> entry : oldvalues.entrySet())
		{
			String name = entry.getKey().stringValue(ctxt);
			Value val = entry.getValue();

			if (!(val instanceof FunctionValue)
					&& !(val instanceof OperationValue))
			{
				LexNameToken oldname = new LexNameToken(classname, name, node.getLocation(), true, false);
				ctxt.put(oldname, val);
			}
		}
	}

	public ObjectValue findObject(APostOpExp node, String classname,
			ObjectValue object)
	{
		if (object.type.getName().getName().equals(classname))
		{
			return object;
		}

		ObjectValue found = null;

		for (ObjectValue ov : object.superobjects)
		{
			found = findObject(node, classname, ov);

			if (found != null)
			{
				break;
			}
		}

		return found;
	}

}
