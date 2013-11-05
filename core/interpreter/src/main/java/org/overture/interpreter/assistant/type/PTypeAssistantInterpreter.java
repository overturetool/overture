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
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.ValueList;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class PTypeAssistantInterpreter extends PTypeAssistantTC
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTypeAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static ValueList getAllValues(PType type, Context ctxt)
			throws ValueException
	{
		if (type instanceof SBasicType)
		{
			return SBasicTypeAssistantInterpreter.getAllValues((SBasicType) type, ctxt);
		} else if (type instanceof SInvariantType)
		{
			return SInvariantTypeAssistantInterpreter.getAllValues((SInvariantType) type, ctxt);
		} else if (type instanceof SMapType)
		{
			if (type instanceof AInMapMapType)
			{
				return AInMapMapTypeAssistantInterpreter.getAllValues((AInMapMapType) type, ctxt);
			} else if (type instanceof AMapMapType)
			{
				return SMapTypeAssistantInterpreter.getAllValues((AMapMapType) type, ctxt);
			}
		} else if (type instanceof AOptionalType)
		{
			return AOptionalTypeAssistantInterpreter.getAllValues((AOptionalType) type, ctxt);
		} else if (type instanceof AProductType)
		{
			return AProductTypeAssistantInterpreter.getAllValues((AProductType) type, ctxt);
		} else if (type instanceof AQuoteType)
		{
			return AQuoteTypeAssistantInterpreter.getAllValues((AQuoteType) type, ctxt);
		} else if (type instanceof ASetType)
		{
			return ASetTypeAssistantInterpreter.getAllValues((ASetType) type, ctxt);
		} else if (type instanceof AUnionType)
		{
			return AUnionTypeAssistantInterpreter.getAllValues((AUnionType) type, ctxt);
		} else if (type instanceof AParameterType)
		{
			return AParameterTypeAssistantInterpreter.getAllValues((AParameterType) type, ctxt);
		}
		throw new ValueException(4, "Cannot get bind values for type " + type, ctxt);
	}

}
