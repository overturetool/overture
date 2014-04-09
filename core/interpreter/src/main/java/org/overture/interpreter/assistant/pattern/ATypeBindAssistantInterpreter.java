package org.overture.interpreter.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.ATypeBind;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.type.PTypeAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.pattern.ATypeBindAssistantTC;

public class ATypeBindAssistantInterpreter extends ATypeBindAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ATypeBindAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static ValueList getBindValues(ATypeBind bind, Context ctxt)
			throws AnalysisException
	{
		return PTypeAssistantInterpreter.getAllValues(bind.getType(), ctxt);
	}

	public static ValueList getValues(ATypeBind bind, ObjectContext ctxt)
	{
		return new ValueList();
	}

}
