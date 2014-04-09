package org.overture.interpreter.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.type.AOptionalTypeAssistant;
import org.overture.ast.types.AOptionalType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.ValueList;

public class AOptionalTypeAssistantInterpreter extends AOptionalTypeAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AOptionalTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static ValueList getAllValues(AOptionalType type, Context ctxt)
			throws AnalysisException
	{
		ValueList list = PTypeAssistantInterpreter.getAllValues(type.getType(), ctxt);
		list.add(new NilValue());
		return list;
	}

}
