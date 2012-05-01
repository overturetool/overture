package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameToken;

public class AIgnorePatternAssistantTC {
	
	private static int var = 1;
	
	public static PType getPossibleTypes(AIgnorePattern pattern) {
		return new AUnknownType(pattern.getLocation(), false);
	}

	public static PExp getMatchingExpression(AIgnorePattern iptrn) {
		LexNameToken any = new LexNameToken("", "any" + var++, iptrn.getLocation());
		return new AVariableExp(iptrn.getLocation(), any, any.getName());
	}

}
