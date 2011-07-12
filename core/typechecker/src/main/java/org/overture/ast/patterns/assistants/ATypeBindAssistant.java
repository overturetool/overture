package org.overture.ast.patterns.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;

public class ATypeBindAssistant {

	public static void typeResolve(ATypeBind typebind,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		typebind.setType(PTypeAssistant.typeResolve(typebind.getType(), null, rootVisitor, question));
		
	}

}
