package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternTypePair;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overturetool.vdmj.typechecker.NameScope;

public class APatternTypePairAssistant {

	public static List<PDefinition> getDefinitions(APatternTypePair result) {
		
		return PPatternAssistant.getDefinitions(result.getPattern(), result.getType(),NameScope.LOCAL);
	}

}
