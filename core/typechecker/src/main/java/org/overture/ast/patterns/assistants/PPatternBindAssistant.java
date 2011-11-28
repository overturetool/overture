package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.visitors.TypeCheckVisitor;


public class PPatternBindAssistant {

	public static void typeCheck(ADefPatternBind node, PType type, TypeCheckVisitor rootVisitor, TypeCheckInfo question)
	{
		
	}
	
	public static List<PDefinition> getDefinitions(ADefPatternBind patternBind) {
				assert (patternBind.getDefs() != null) :
			"PatternBind must be type checked before getDefinitions";

		return patternBind.getDefs();
	}
	
}
