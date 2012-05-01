package org.overture.ast.patterns.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.typechecker.NameScope;

public class AIdentifierPatternAssistantTC {

	public static PType getPossibleTypes(AIdentifierPattern pattern) {
		return new AUnknownType(pattern.getLocation(), false);
	}

	public static List<PDefinition> getDefinitions(AIdentifierPattern rp,
			PType ptype, NameScope scope) {
		AIdentifierPattern idPattern = (AIdentifierPattern) rp;
		List<PDefinition> defs = new ArrayList<PDefinition>();
		defs.add(new ALocalDefinition(idPattern.getLocation(), scope, false, null, null, ptype, false, idPattern.getName().clone()));
		return defs;
	}

}
