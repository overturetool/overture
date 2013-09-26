package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ALocalDefinitionAssistantTC
{

	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ALocalDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void setValueDefinition(ALocalDefinition ld)
	{
		ld.setValueDefinition(true);

	}

//	public static void typeResolve(ALocalDefinition d,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question)
//	{
//
//		if (d.getType() != null)
//		{
//			d.setType(PTypeAssistantTC.typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(d), null, rootVisitor, question));
//		}
//
//	}

}
