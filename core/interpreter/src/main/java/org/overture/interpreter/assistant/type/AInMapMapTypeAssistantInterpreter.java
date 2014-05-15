package org.overture.interpreter.assistant.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.AInMapMapType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;

public class AInMapMapTypeAssistantInterpreter
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInMapMapTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

//	public static ValueList getAllValues(AInMapMapType type, Context ctxt)
//			throws AnalysisException
//	{
//		ValueList maps = SMapTypeAssistantInterpreter.getAllValues(type, ctxt);
//		ValueList result = new ValueList();
//
//		for (Value map : maps)
//		{
//			MapValue vm = (MapValue) map;
//
//			if (vm.values.isInjective())
//			{
//				result.add(vm);
//			}
//		}
//
//		return result;
//	}

}
