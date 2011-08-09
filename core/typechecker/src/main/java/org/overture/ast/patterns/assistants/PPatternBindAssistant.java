package org.overture.ast.patterns.assistants;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PPatternBind;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;
import org.overture.typecheck.visitors.TypeCheckVisitor;
import org.overturetool.vdmj.typechecker.NameScope;


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
