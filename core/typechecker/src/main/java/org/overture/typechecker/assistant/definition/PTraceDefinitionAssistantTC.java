package org.overture.typechecker.assistant.definition;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PTraceDefinitionAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PTraceDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static void typeCheck(List<PTraceDefinition> term,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		
		for (PTraceDefinition def: term)
		{
			def.apply(rootVisitor, question);
		}
		
	}

}
