package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.AStringLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexStringToken;

public class AStringPatternAssistantTC {

	public static PType getPossibleTypes(AStringPattern pattern) {
		ASeqSeqType t = new ASeqSeqType(pattern.getLocation(), false, false);
		t.setSeqof( new AUnknownType(pattern.getLocation(), false));
		return t;
	}

	public static PExp getMatchingExpression(AStringPattern sp) {
		LexStringToken v = sp.getValue();
		return new AStringLiteralExp(null, sp.getLocation(), (LexStringToken) v.clone());
	}

}
