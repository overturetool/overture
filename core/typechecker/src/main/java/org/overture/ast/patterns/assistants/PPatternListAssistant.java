package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class PPatternListAssistant {

	public static void typeResolve(List<PPattern> pp,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		for (PPattern pattern : pp) {
			PPatternAssistant.typeResolve(pattern, rootVisitor, question);
		}
		
	}

}
