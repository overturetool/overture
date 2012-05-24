package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexRealToken;

public class ARealPatternAssistantTC {

	public static PType getPossibleTypes(ARealPattern pattern) {
		return AstFactory.newARealNumericBasicType(pattern.getLocation());
	}

	public static PExp getMatchingExpression(ARealPattern rp) {
		LexRealToken v = rp.getValue();
		return AstFactory.newARealLiteralExp((LexRealToken) v.clone());
	}

}
