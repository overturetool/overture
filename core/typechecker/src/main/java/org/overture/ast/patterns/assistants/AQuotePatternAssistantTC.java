package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.AQuoteLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexQuoteToken;

public class AQuotePatternAssistantTC {

	public static PType getPossibleTypes(AQuotePattern pattern) {
		return new AQuoteType(pattern.getLocation(), false, ((AQuotePattern) pattern).getValue().clone());
	}

	public static PExp getMatchingExpression(AQuotePattern qp) {
		LexQuoteToken v = qp.getValue();
		return new AQuoteLiteralExp(null, qp.getLocation(),v.clone());
	}

}
