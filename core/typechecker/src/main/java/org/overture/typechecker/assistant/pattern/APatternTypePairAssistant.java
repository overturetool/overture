package org.overture.typechecker.assistant.pattern;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class APatternTypePairAssistant {

	public static List<PDefinition> getDefinitions(APatternTypePair result) {
		
		return PPatternAssistantTC.getDefinitions(result.getPattern(), result.getType(),NameScope.LOCAL);
	}

	public static void typeResolve(APatternTypePair result,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		if (result.getResolved() ) return; else { result.setResolved(true); }
		result.setType(PTypeAssistantTC.typeResolve(result.getType(),null,rootVisitor,question));
		
	}

}
