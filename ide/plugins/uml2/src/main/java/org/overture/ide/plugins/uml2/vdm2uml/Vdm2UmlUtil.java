package org.overture.ide.plugins.uml2.vdm2uml;

import java.util.LinkedList;

import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.VisibilityKind;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class Vdm2UmlUtil
{

	public static VisibilityKind convertAccessSpecifierToVisibility(
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{

		if (PAccessSpecifierAssistantTC.isPrivate(accessSpecifier))
		{
			return VisibilityKind.PRIVATE_LITERAL;
		} else if (PAccessSpecifierAssistantTC.isProtected(accessSpecifier))
		{
			return VisibilityKind.PROTECTED_LITERAL;
		}

		return VisibilityKind.PUBLIC_LITERAL;

	}

	public static int extractUpper(PType type)
	{
		if (!isOptional(type)
				&& (type instanceof ASetType || type instanceof SSeqType || type instanceof SMapType))
		{
			return LiteralUnlimitedNatural.UNLIMITED;

		}

		return 1;
	}

	public static int extractLower(PType type)
	{
		if (type instanceof ASetType || type instanceof ASeqSeqType
				|| type instanceof SMapType || isOptional(type))// PTypeAssistantTC.isType(type, AOptionalType.class))
		{
			return 0;
		}

		return 1;
	}

	public static boolean extractIsOrdered(PType type)
	{
		Boolean isOrdered = false;

		if (PTypeAssistantTC.isType(type, ASetType.class))
		{
			isOrdered = false;
		} else if (PTypeAssistantTC.isType(type, ASeqSeqType.class))
		{
			isOrdered = true;
		} else if (PTypeAssistantTC.isType(type, ASeq1SeqType.class))
		{
			isOrdered = true;
		} else if (PTypeAssistantTC.isType(type, SMapType.class))
		{
			isOrdered = true;
		} else if (PTypeAssistantTC.isType(type, AOptionalType.class))
		{

		}

		return isOrdered;
	}

	public static boolean extractIsUnique(PType type)
	{
		Boolean isUnique = true;

		if (PTypeAssistantTC.isType(type, ASetType.class))
		{
		} else if (PTypeAssistantTC.isType(type, ASeqSeqType.class))
		{
			isUnique = false;
		} else if (PTypeAssistantTC.isType(type, ASeq1SeqType.class))
		{
			isUnique = false;
		} else if (PTypeAssistantTC.isType(type, SMapType.class))
		{
			isUnique = false;
		} else if (PTypeAssistantTC.isType(type, AOptionalType.class))
		{
		}

		return isUnique;
	}

	public static boolean isClassActive(SClassDefinition sClass)
	{

		for (PDefinition def : sClass.getDefinitions())
		{
			if (AThreadDefinition.kindPDefinition.equals(def.kindPDefinition()))
				return true;
		}
		return false;
	}

	public static boolean hasSubclassResponsabilityDefinition(
			LinkedList<PDefinition> definitions)
	{

		for (PDefinition pDefinition : definitions)
		{
			if (isSubclassResponsability(pDefinition))
				return true;
		}

		return false;
	}

	private static boolean isSubclassResponsability(PDefinition pDefinition)
	{

		if (PDefinitionAssistantTC.isOperation(pDefinition))
		{
			if (pDefinition instanceof AExplicitOperationDefinition)
			{
				return ASubclassResponsibilityStm.kindPStm
						.equals(((AExplicitOperationDefinition)pDefinition).getBody().kindPStm());
			} else if (pDefinition instanceof AImplicitOperationDefinition)
			{
				PStm body = ((AImplicitOperationDefinition) pDefinition).getBody();
				// implicit operations may or may not have a body
				if (body == null)
				{
					return true;
				} else
				{
					return ASubclassResponsibilityStm.kindPStm.equals(body.kindPStm());
				}
			}
		}

		return false;
	}

	public static boolean hasPolymorphic(AExplicitFunctionDefinition pDefinition)
	{

		AFunctionType funcType = (AFunctionType) PDefinitionAssistantTC.getType(pDefinition);

		for (PType t : funcType.getParameters())
		{
			if (PTypeAssistantTC.isType(t, AParameterType.class))
			{
				return true;
			}
		}

		if (PTypeAssistantTC.isType(funcType.getResult(), AParameterType.class))
		{
			return true;
		}

		return false;
	}

	public static boolean isUnionOfQuotes(AUnionType type)
	{
		try
		{
			for (PType t : type.getTypes())
			{
				if (!PTypeAssistantTC.isType(t, AQuoteType.class))
				{
					return false;
				}
			}
		} catch (Error t)//Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}

	public static boolean isOptional(PType defType)
	{
		return (defType instanceof AOptionalType);

	}

}
