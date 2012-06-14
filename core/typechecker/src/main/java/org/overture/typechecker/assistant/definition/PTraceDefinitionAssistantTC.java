package org.overture.typechecker.assistant.definition;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;

public class PTraceDefinitionAssistantTC {

	public static void typeCheck(List<PTraceDefinition> term,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws Throwable {
		
		for (PTraceDefinition def: term)
		{
			def.apply(rootVisitor, question);
		}
		
	}

}
