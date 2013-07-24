package org.overture.typechecker.assistant.pattern;

import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ANilPatternAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ANilPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PType getPossibleTypes(ANilPattern pattern) {
		return AstFactory.newAOptionalType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()));
	}

	public static PExp getMatchingExpression(ANilPattern np) {
		return AstFactory.newANilExp(np.getLocation()); 
	}

}
