package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexRealToken;

public class ARealPatternAssistantTC {

	public static PType getPossibleTypes(ARealPattern pattern) {
		return new ARealNumericBasicType(pattern.getLocation(), false);
	}

	public static PExp getMatchingExpression(ARealPattern rp) {
		LexRealToken v = rp.getValue();
		return new ARealLiteralExp(null, rp.getLocation(), (LexRealToken) v.clone());
	}

}
