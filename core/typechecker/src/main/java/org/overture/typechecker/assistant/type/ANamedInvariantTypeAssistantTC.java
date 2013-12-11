package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.type.ANamedInvariantTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class ANamedInvariantTypeAssistantTC extends
		ANamedInvariantTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANamedInvariantTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}


	public static boolean equals(ANamedInvariantType type, Object other)
	{
		other = PTypeAssistantTC.deBracket(other);

		if (other instanceof ANamedInvariantType)
		{
			ANamedInvariantType nother = (ANamedInvariantType) other;
			return type.getName().equals(nother.getName());
		}

		return false;
	}

	public static AUnionType getUnion(ANamedInvariantType type)
	{
		return PTypeAssistantTC.getUnion(type.getType());
	}

	public static AFunctionType getFunction(ANamedInvariantType type)
	{
		return PTypeAssistantTC.getFunction(type.getType());
	}

	public static boolean narrowerThan(ANamedInvariantType type,
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{

		if (type.getInNarrower())
		{
			return false;
		}

		type.setInNarrower(true);
		boolean result = false;

		if (type.getDefinitions().size() > 0)
		{
			for (PDefinition d : type.getDefinitions())
			{
				if (PAccessSpecifierAssistantTC.narrowerThan(d.getAccess(), accessSpecifier))
				{
					result = true;
					break;
				}
			}
		} else if (type.getType().getDefinitions().size() == 0)
		{
			result = PTypeAssistantTC.narrowerThan(type, accessSpecifier)
					|| PTypeAssistantTC.narrowerThanBaseCase(type, accessSpecifier);
		} else
		{
			for (PDefinition d : type.getType().getDefinitions())
			{
				if (PAccessSpecifierAssistantTC.narrowerThan(d.getAccess(), accessSpecifier))
				{
					result = true;
					break;
				}
			}

		}

		type.setInNarrower(false);
		return result;
	}

}
