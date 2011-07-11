package org.overture.ast.patterns.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class ARecordPatternAssistant {

	public static void typeResolve(ARecordPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternAssistant.typeResolve(pattern.getPlist(),rootVisitor,question);
			pattern.setType(PTypeAssistant.typeResolve(pattern.getType(),null, rootVisitor,question));
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(ARecordPattern pattern) {
		PTypeAssistant.unResolve(pattern.getType());
		pattern.setResolved(false);		
	}
	
}
