package org.overture.typechecker.assistant.pattern;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;

public class AIdentifierPatternAssistantTC {

	public static PType getPossibleTypes(AIdentifierPattern pattern) {
		return AstFactory.newAUnknownType(pattern.getLocation());
	}

	public static List<PDefinition> getAllDefinitions(AIdentifierPattern rp,
			PType ptype, NameScope scope) {
		List<PDefinition> defs = new ArrayList<PDefinition>();
		defs.add(AstFactory.newALocalDefinition(rp.getLocation(), rp.getName().clone(), scope, ptype));
		return defs;
	}

	public static PExp getMatchingExpression(AIdentifierPattern idp) {
		return AstFactory.newAVariableExp(idp.getName().clone());
	}

}
