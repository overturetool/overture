package org.overture.ast.types.assistants;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPatternBind;


public class PPatternBindAssistant {

	public static List<PDefinition> getDefinitions(PPatternBind patternBind) {
		assert false: "INVESTIGATE PATTERN BIND"; 
		return null;
//		assert (patternBind.getDefs() != null) :
//			"PatternBind must be type checked before getDefinitions";
//
//		return patternBind.getDefs();
	}

}
