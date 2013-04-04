package org.overture.interpreter.assistant.type;

import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class PTypeAssistantInterpreter extends PTypeAssistantTC
{

	public static ValueList getAllValues(PType type, Context ctxt) throws ValueException
	{
		switch (type.kindPType())
		{
			case SBasicType.kindPType:
				return SBasicTypeAssistantInterpreter.getAllValues((SBasicType) type,ctxt);
			case SInvariantType.kindPType:
				return SInvariantTypeAssistantInterpreter.getAllValues((SInvariantType)type,ctxt);
			case SMapType.kindPType:
				switch (((SMapType) type).kindSMapType())
				{
					case AInMapMapType.kindSMapType:
						return AInMapMapTypeAssistantInterpreter.getAllValues((AInMapMapType) type, ctxt);
					case AMapMapType.kindSMapType:
						return SMapTypeAssistantInterpreter.getAllValues((AMapMapType) type, ctxt);
				}
			case AOptionalType.kindPType:
				return AOptionalTypeAssistantInterpreter.getAllValues((AOptionalType)type,ctxt);
			case AProductType.kindPType:
				return AProductTypeAssistantInterpreter.getAllValues((AProductType)type,ctxt);
			case AQuoteType.kindPType:
				return AQuoteTypeAssistantInterpreter.getAllValues((AQuoteType)type,ctxt);
			case ASetType.kindPType:
				return ASetTypeAssistantInterpreter.getAllValues((ASetType)type,ctxt);
			case AUnionType.kindPType:
				return AUnionTypeAssistantInterpreter.getAllValues((AUnionType)type,ctxt);
			case AParameterType.kindPType:
				return AParameterTypeAssistantInterpreter.getAllValues((AParameterType) type, ctxt);
			default:
				throw new ValueException(4, "Cannot get bind values for type " + type, ctxt);
		}
	}

}
