package org.overture.typechecker.assistant.pattern;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ADefPatternBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.visitor.TypeCheckVisitor;


public class PPatternBindAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PPatternBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static void typeCheck(ADefPatternBind node, PType type, TypeCheckVisitor rootVisitor, TypeCheckInfo question)
	{
		
	}
	
	public static List<PDefinition> getDefinitions(ADefPatternBind patternBind) {
				assert (patternBind.getDefs() != null) :
			"PatternBind must be type checked before getDefinitions";

		return patternBind.getDefs();
	}
	
}
