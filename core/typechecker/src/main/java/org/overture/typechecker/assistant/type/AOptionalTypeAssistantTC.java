package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.type.AOptionalTypeAssistant;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AOptionalTypeAssistantTC extends AOptionalTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AOptionalTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public static boolean isType(AOptionalType b,
			Class<? extends PType> typeclass)
	{

		if (typeclass.equals(AVoidType.class))
		{
			return false; // Optionals are never void
		}

		return PTypeAssistantTC.isType(b.getType(), typeclass);
	}

	public static AFunctionType getFunction(AOptionalType type)
	{
		return PTypeAssistantTC.getFunction(type.getType());
	}

	public static boolean isOperation(AOptionalType type)
	{
		return PTypeAssistantTC.isOperation(type.getType());
	}

	public static AOperationType getOperation(AOptionalType type)
	{
		return PTypeAssistantTC.getOperation(type.getType());
	}

	public static boolean isSeq(AOptionalType type)
	{
		return PTypeAssistantTC.isSeq(type.getType());
	}

	public static SSeqType getSeq(AOptionalType type)
	{
		return PTypeAssistantTC.getSeq(type.getType());
	}

	public static boolean isMap(AOptionalType type)
	{
		return PTypeAssistantTC.isMap(type.getType());
	}

	public static SMapType getMap(AOptionalType type)
	{
		return af.createPTypeAssistant().getMap(type.getType());
	}

}
