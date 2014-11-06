package org.overture.interpreter.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.types.PType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class PTypeAssistantInterpreter extends PTypeAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public ValueList getAllValues(PType type, Context ctxt)
			throws AnalysisException
	{
		try
		{
			return type.apply(af.getAllValuesCollector(), ctxt);// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			throw new ValueException(4, "Cannot get bind values for type "
					+ type, ctxt);
		}
	}

}
