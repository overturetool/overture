package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.type.AParameterTypeAssistant;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AParameterTypeAssistantTC extends AParameterTypeAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AParameterTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

//	public static PType typeResolve(AParameterType type, ATypeDefinition root,
//			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question)
//	{
//
//		if (type.getResolved())
//		{
//			return type;
//		} else
//		{
//			type.setResolved(true);
//		}
//
//		PDefinition p = question.env.findName(type.getName(), NameScope.NAMES);
//
//		if (p == null
//				|| !(question.assistantFactory.createPDefinitionAssistant().getType(p) instanceof AParameterType))
//		{
//			TypeCheckerErrors.report(3433, "Parameter type @" + type.getName()
//					+ " not defined", type.getLocation(), type);
//		}
//
//		return type;
//	}

}
