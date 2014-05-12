package org.overture.interpreter.assistant.type;

import org.overture.ast.assistant.type.ANamedInvariantTypeAssistant;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

public class ANamedInvariantTypeAssistantInterpreter extends ANamedInvariantTypeAssistant

{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANamedInvariantTypeAssistantInterpreter(
			IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

//	public static ValueList getAllValues(ANamedInvariantType type, Context ctxt)
//			throws AnalysisException
//	{
//		ValueList raw = PTypeAssistantInterpreter.getAllValues(type.getType(), ctxt);
//		boolean checks = Settings.invchecks;
//		Settings.invchecks = true;
//
//		ValueList result = new ValueList();
//		for (Value v : raw)
//		{
//			try
//			{
//				result.add(new InvariantValue(type, v, ctxt));
//			} catch (ValueException e)
//			{
//				// Raw value not in type because of invariant
//			}
//		}
//
//		Settings.invchecks = checks;
//		return result;
//	}

}
