package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.PBindAssistantTC;

public class PBindAssistantInterpreter extends PBindAssistantTC
{

	public static ValueList getBindValues(PBind bind, Context ctxt) throws ValueException
	{
		switch (bind.kindPBind())
		{
			case SET:
				return ASetBindAssistantInterpreter.getBindValues((ASetBind) bind, ctxt);
			case TYPE:
				return ATypeBindAssistantInterpreter.getBindValues((ATypeBind)bind,ctxt);
			default:
				assert false : "Should not happen";
				return null;
		}
	}

	public static ValueList getValues(PBind bind, ObjectContext ctxt)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
