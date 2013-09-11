package org.overture.typechecker.assistant.definition;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

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

	public static void typeResolve(ALocalDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{

		if (d.getType() != null)
		{
			d.setType(PTypeAssistantTC.typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(d), null, rootVisitor, question));
		}

	}

	public static boolean isFunction(ALocalDefinition def)
	{
		return (def.getValueDefinition() || PTypeAssistantTC.isType(af.createPDefinitionAssistant().getType(def), AParameterType.class)) ? false
				: PTypeAssistantTC.isFunction(af.createPDefinitionAssistant().getType(def));
	}

}
