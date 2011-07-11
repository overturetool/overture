package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class AOptionalTypeAssistant {

	public static PType typeResolve(AOptionalType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else { type.setResolved(true); }
		type.setType(PTypeAssistant.typeResolve(type.getType(), root, rootVisitor, question));
		
		if (root != null)  root.setInfinite(false);	// Could be nil
		return type;
	}

}
