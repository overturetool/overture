package org.overture.typechecker.assistant.definition;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ARenamedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARenamedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static void markUsed(ARenamedDefinition d)
//	{
//		d.setUsed(true);
//		PDefinitionAssistantTC.markUsed(d.getDef());
//
//	}

//	public static void typeResolve(ARenamedDefinition d,
//			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) throws AnalysisException
//	{
//		PDefinitionAssistantTC.typeResolve(d.getDef(), rootVisitor, question);
//	}

	// public static boolean isUsed(ARenamedDefinition u) {
	// return PDefinitionAssistantTC.isUsed(u.getDef());
	// }

}
