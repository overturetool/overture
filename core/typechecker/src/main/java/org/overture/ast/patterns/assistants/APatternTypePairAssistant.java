package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.assistants.PPatternTCAssistant;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.typechecker.NameScope;

public class APatternTypePairAssistant {

	public static List<PDefinition> getDefinitions(APatternTypePair result) {
		
		return PPatternTCAssistant.getDefinitions(result.getPattern(), result.getType(),NameScope.LOCAL);
	}

	public static void typeResolve(APatternTypePair result,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		if (result.getResolved() ) return; else { result.setResolved(true); }
		result.setType(PTypeAssistant.typeResolve(result.getType(),null,rootVisitor,question));
		
	}

}
