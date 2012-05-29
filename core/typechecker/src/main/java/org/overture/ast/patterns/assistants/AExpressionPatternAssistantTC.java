package org.overture.ast.patterns.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.typechecker.NameScope;

public class AExpressionPatternAssistantTC {

	public static PType getPossibleTypes(AExpressionPattern pattern) {
		return AstFactory.newAUnknownType(pattern.getLocation());
	}

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
			PPatternAssistantTC.unResolve(pattern);
			throw e;
		}
		
	}

	public static PExp getMatchingExpression(AExpressionPattern p) {
		return p.getExp();
	}
}
