package org.overture.ast.statements.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;

public class AExternalClauseAssistantTC {

	public static void typeResolve(AExternalClause clause,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		clause.setType(PTypeAssistant.typeResolve(clause.getType(),null,rootVisitor,question));
		
	}

}
