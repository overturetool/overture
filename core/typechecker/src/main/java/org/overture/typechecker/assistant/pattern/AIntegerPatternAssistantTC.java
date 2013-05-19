package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.type.SNumericBasicTypeAssistantTC;

public class AIntegerPatternAssistantTC {

	public static PType getPossibleTypes(AIntegerPattern pattern) {
		return SNumericBasicTypeAssistantTC.typeOf(pattern.getValue().getValue(), pattern.getLocation());
	}

	public static PExp getMatchingExpression(AIntegerPattern intptrn) {
		return AstFactory.newAIntLiteralExp((LexIntegerToken) intptrn.getValue().clone());
	}

}
