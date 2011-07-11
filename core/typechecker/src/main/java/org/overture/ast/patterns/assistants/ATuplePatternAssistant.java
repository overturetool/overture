package org.overture.ast.patterns.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.types.PType;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class ATuplePatternAssistant {

	public static void typeResolve(ATuplePattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternAssistant.typeResolve(pattern.getPlist(), rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(ATuplePattern pattern) {

		PPatternAssistant.unResolve(pattern.getPlist());
		pattern.setResolved(false);
		
	}

}
