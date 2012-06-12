package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexCharacterToken;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.types.PType;

public class ACharacterPatternAssistantTC {

	public static PType getPossibleType(ACharacterPattern pattern) {
		return AstFactory.newACharBasicType(pattern.getLocation());
	}

	public static PExp getMatchingExpression(ACharacterPattern chr) {
		LexCharacterToken v = chr.getValue();
		return AstFactory.newACharLiteralExp((LexCharacterToken) v.clone());
	}

}
