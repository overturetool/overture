package org.overture.ast.definitions.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class PTraceDefinitionAssistant {

	public static void typeCheck(List<PTraceDefinition> term,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		for (PTraceDefinition def: term)
		{
			def.apply(rootVisitor, question);
		}
		
	}

}
