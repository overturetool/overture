package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.ACharLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexCharacterToken;

public class ACharacterPatternAssistantTC {

	public static PType getPossibleType(ACharacterPattern pattern) {
		return new ACharBasicType(pattern.getLocation(), false);
	}

	public static PExp getMatchingExpression(ACharacterPattern chr) {
		LexCharacterToken v = chr.getValue();
		return new ACharLiteralExp(null, chr.getLocation(), (LexCharacterToken) v.clone());
	}

}
