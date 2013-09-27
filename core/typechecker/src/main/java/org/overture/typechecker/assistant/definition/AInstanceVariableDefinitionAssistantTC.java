package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AInstanceVariableDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInstanceVariableDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static void typeResolve(AInstanceVariableDefinition d,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question)
//	{
//
//		try
//		{
//			d.setType(PTypeAssistantTC.typeResolve(d.getType(), null, rootVisitor, question));
//		} catch (TypeCheckException e)
//		{
//			PTypeAssistantTC.unResolve(d.getType());
//			throw e;
//		}
//
//	}

	public static void initializedCheck(AInstanceVariableDefinition ivd)
	{
		if (!ivd.getInitialized()
				&& !PAccessSpecifierAssistantTC.isStatic(ivd.getAccess()))
		{
			TypeCheckerErrors.warning(5001, "Instance variable '"
					+ ivd.getName() + "' is not initialized", ivd.getLocation(), ivd);
		}

	}

}
