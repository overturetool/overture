package org.overture.interpreter.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;

public class AUnionTypeAssistantInterpreter extends AUnionTypeAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUnionTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static ValueList getAllValues(AUnionType utype, Context ctxt)
			throws AnalysisException
	{
		ValueList v = new ValueList();

		for (PType type : utype.getTypes())
		{
			v.addAll(PTypeAssistantInterpreter.getAllValues(type, ctxt));
		}

		return v;
	}

}
