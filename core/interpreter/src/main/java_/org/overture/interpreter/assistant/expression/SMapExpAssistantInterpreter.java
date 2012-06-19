package org.overture.interpreter.assistant.expression;

import org.overture.ast.expressions.AMapCompMapExp;
import org.overture.ast.expressions.AMapEnumMapExp;
import org.overture.ast.expressions.SMapExp;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.expression.SMapExpAssistantTC;

public class SMapExpAssistantInterpreter extends SMapExpAssistantTC
{

	public static ValueList getValues(SMapExp exp, ObjectContext ctxt)
	{
		switch (exp.kindSMapExp())
		{
			case MAPCOMP:
				return AMapCompMapExpAssistantInterpreter.getValues((AMapCompMapExp)exp,ctxt);
			case MAPENUM:
				return AMapEnumMapExpAssistantInterpreter.getValues((AMapEnumMapExp)exp,ctxt);
			default:
				return new ValueList();
		}
	}

}
