package org.overture.ast.patterns.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.types.PType;

import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.typechecker.NameScope;


public class AExpressionPatternAssistant {

	public static void typeResolve(AExpressionPattern pattern, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			question.qualifiers = null;
			question.scope = NameScope.NAMESANDSTATE;
			pattern.getExp().apply(rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			PPatternTCAssistant.unResolve(pattern);
			throw e;
		}
		
	}

}
