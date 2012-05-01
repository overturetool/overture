package org.overture.ast.patterns.assistants;

import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;

public class ANilPatternAssistantTC {

	public static PType getPossibleTypes(ANilPattern pattern) {
		return new AOptionalType(pattern.getLocation(), false,null, new AUnknownType(pattern.getLocation(), false));
	}

	public static PExp getMatchingExpression(ANilPattern np) {
		return new ANilExp(null, np.getLocation()); 
	}

}
