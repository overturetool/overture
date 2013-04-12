package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.lex.LexNameToken;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.FunctionValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.OperationValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueMap;

public class APostOpExpAssistant
{

	public static void populate(APostOpExp node,Context ctxt, String classname, ValueMap oldvalues) throws ValueException
	{
		for (Value var: oldvalues.keySet())
		{
			String name = var.stringValue(ctxt);
			Value val = oldvalues.get(var);

			if (!(val instanceof FunctionValue) &&
				!(val instanceof OperationValue))
			{
				LexNameToken oldname = new LexNameToken(classname, name, node.getLocation(), true, false);
				ctxt.put(oldname, val);
			}
		}
	}

	public static ObjectValue findObject(APostOpExp node, String classname, ObjectValue object)
	{
		if (object.type.getName().getName().equals(classname))
		{
			return object;
		}

		ObjectValue found = null;

		for (ObjectValue ov: object.superobjects)
		{
			found = findObject(node,classname, ov);

			if (found != null)
			{
				break;
			}
		}

		return found;
	}

}
