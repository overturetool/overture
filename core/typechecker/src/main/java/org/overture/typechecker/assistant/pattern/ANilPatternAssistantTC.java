package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.types.PType;

public class ANilPatternAssistantTC {

	public static PType getPossibleTypes(ANilPattern pattern) {
		return AstFactory.newAOptionalType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()));
	}

	public static PExp getMatchingExpression(ANilPattern np) {
		return AstFactory.newANilExp(np.getLocation()); 
	}

}
