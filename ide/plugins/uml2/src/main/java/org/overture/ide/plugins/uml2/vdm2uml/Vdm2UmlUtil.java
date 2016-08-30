/*
 * #%~
 * UML2 Translator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
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
import org.overture.ast.types.SSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.InterpreterAssistantFactory;

public class Vdm2UmlUtil
{
	public final static IInterpreterAssistantFactory assistantFactory = new InterpreterAssistantFactory();

	public static VisibilityKind convertAccessSpecifierToVisibility(
			AAccessSpecifierAccessSpecifier accessSpecifier)
	{

		if (assistantFactory.createPAccessSpecifierAssistant().isPrivate(accessSpecifier))
		{
			return VisibilityKind.PRIVATE_LITERAL;
		} else if (assistantFactory.createPAccessSpecifierAssistant().isProtected(accessSpecifier))
		{
			return VisibilityKind.PROTECTED_LITERAL;
		}

		return VisibilityKind.PUBLIC_LITERAL;

	}

	public static int extractUpper(PType type)
	{
		if (!isOptional(type)
				&& (type instanceof SSetType || type instanceof SSeqType || type instanceof SMapType))
		{
			return LiteralUnlimitedNatural.UNLIMITED;

		}

		return 1;
	}

	public static int extractLower(PType type)
	{
		if (type instanceof SSetType || type instanceof ASeqSeqType
				|| type instanceof SMapType || isOptional(type))// PTypeAssistantTC.isType(type, AOptionalType.class))
		{
			return 0;
		}

		return 1;
	}

	public static boolean extractIsOrdered(PType type)
	{
		Boolean isOrdered = false;

		if (assistantFactory.createPTypeAssistant().isType(type, SSetType.class))
		{
			isOrdered = false;
		} else if (assistantFactory.createPTypeAssistant().isType(type, ASeqSeqType.class))
		{
			isOrdered = true;
		} else if (assistantFactory.createPTypeAssistant().isType(type, ASeq1SeqType.class))
		{
			isOrdered = true;
		} else if (assistantFactory.createPTypeAssistant().isType(type, SMapType.class))
		{
			isOrdered = true;
		} else if (assistantFactory.createPTypeAssistant().isType(type, AOptionalType.class))
		{

		}

		return isOrdered;
	}

	public static boolean extractIsUnique(PType type)
	{
		Boolean isUnique = true;

		if (assistantFactory.createPTypeAssistant().isType(type, SSetType.class))
		{
		} else if (assistantFactory.createPTypeAssistant().isType(type, ASeqSeqType.class))
		{
			isUnique = false;
		} else if (assistantFactory.createPTypeAssistant().isType(type, ASeq1SeqType.class))
		{
			isUnique = false;
		} else if (assistantFactory.createPTypeAssistant().isType(type, SMapType.class))
		{
			isUnique = false;
		} else if (assistantFactory.createPTypeAssistant().isType(type, AOptionalType.class))
		{
		}

		return isUnique;
	}

	public static boolean isClassActive(SClassDefinition sClass)
	{

		for (PDefinition def : sClass.getDefinitions())
		{
			if (def instanceof AThreadDefinition)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasSubclassResponsabilityDefinition(
			LinkedList<PDefinition> definitions)
	{

		for (PDefinition pDefinition : definitions)
		{
			if (isSubclassResponsability(pDefinition))
			{
				return true;
			}
		}

		return false;
	}

	private static boolean isSubclassResponsability(PDefinition pDefinition)
	{

		if (assistantFactory.createPDefinitionAssistant().isOperation(pDefinition))
		{
			if (pDefinition instanceof AExplicitOperationDefinition)
			{
				return ((AExplicitOperationDefinition) pDefinition).getBody() instanceof ASubclassResponsibilityStm;
			} else if (pDefinition instanceof AImplicitOperationDefinition)
			{
				PStm body = ((AImplicitOperationDefinition) pDefinition).getBody();
				// implicit operations may or may not have a body
				if (body == null)
				{
					return true;
				} else
				{
					return body instanceof ASubclassResponsibilityStm;
				}
			}
		}

		return false;
	}

	public static boolean hasPolymorphic(AExplicitFunctionDefinition pDefinition)
	{

		AFunctionType funcType = (AFunctionType) assistantFactory.createPDefinitionAssistant().getType(pDefinition);

		for (PType t : funcType.getParameters())
		{
			if (assistantFactory.createPTypeAssistant().isType(t, AParameterType.class))
			{
				return true;
			}
		}

		if (assistantFactory.createPTypeAssistant().isType(funcType.getResult(), AParameterType.class))
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
				if (!assistantFactory.createPTypeAssistant().isType(t, AQuoteType.class))
				{
					return false;
				}
			}
		} catch (Error t)// Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}

	public static boolean isOptional(PType defType)
	{
		return defType instanceof AOptionalType;

	}

}
