package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexBooleanToken;

public class ABooleanPatternAssistantTC {

	public static PType getPossibleType(ABooleanPattern pattern) {
		return new ABooleanBasicType(pattern.getLocation(), false);
	}

	public static PExp getMatchingExpression(ABooleanPattern bp) {
		LexBooleanToken tok = bp.getValue();
		ABooleanConstExp res = new ABooleanConstExp(null, bp.getLocation(), (LexBooleanToken) tok.clone());
		return res;
	}

}
