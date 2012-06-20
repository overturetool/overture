package org.overture.interpreter.assistant.pattern;

import org.overture.ast.patterns.ATypeBind;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.ATypeBindAssistantTC;

public class ATypeBindAssistantInterpreter extends ATypeBindAssistantTC
{

	public static ValueList getBindValues(ATypeBind bind, Context ctxt) throws ValueException
	{
		return PTypeAssistantInterpreter.getAllValues(bind.getType(),ctxt);
	}

}
