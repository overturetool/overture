package org.overture.ast.patterns.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.types.PType;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class AUnionPatternAssistant {

	public static void typeResolve(AUnionPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternAssistant.typeResolve(pattern.getLeft(), rootVisitor, question);
			PPatternAssistant.typeResolve(pattern.getRight(), rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(AUnionPattern pattern) {
		PPatternAssistant.unResolve(pattern.getLeft());
		PPatternAssistant.unResolve(pattern.getRight());
		pattern.setResolved(false);
		
	}

}
