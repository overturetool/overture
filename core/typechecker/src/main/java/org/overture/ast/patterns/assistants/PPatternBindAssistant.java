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
		node.setDefs(null);

		if (node.getBind() != null)
		{
			if (node.getBind() instanceof ATypeBind)
			{
				ATypeBind typebind = (ATypeBind)node.getBind();
				typebind.apply(rootVisitor, question);

				if (!TypeComparator.compatible(typebind.getType(), type))
				{
					TypeCheckerErrors.report(3198, "Type bind not compatible with expression", node.getBind().getLocation(), node.getBind());
					TypeCheckerErrors.detail2("Bind", typebind.getType(), "Exp", type);
				}
			}
			else
			{
				ASetBind setbind = (ASetBind)node.getBind();
				ASetType settype = PTypeAssistant.getSet(setbind.getSet().apply(rootVisitor, question));
				if (!TypeComparator.compatible(type, settype.getSetof()))
				{
					TypeCheckerErrors.report(3199, "Set bind not compatible with expression", node.getBind().getLocation(), node.getBind());
					TypeCheckerErrors.detail2("Bind", settype.getSetof(), "Exp", type);
				}
			}

			PDefinition def =new AMultiBindListDefinition(node.getBind().getLocation(), null, null, false, null, 
					PAccessSpecifierAssistant.getDefault(), null, PBindAssistant.getMultipleBindList(node.getBind()), null);

			def.apply(rootVisitor, question);
			LinkedList<PDefinition> defs = new LinkedList<PDefinition>();
			defs.add(def);
			node.setDefs(defs);
		}
		else
		{
			assert (type != null) :
					"Can't typecheck a pattern without a type";

			PPatternTCAssistant.typeResolve(node.getPattern(), rootVisitor, question);
			node.setDefs(PPatternTCAssistant.getDefinitions(node.getPattern(), type, NameScope.LOCAL));
		}
		
	}
	
	public static List<PDefinition> getDefinitions(PPatternBind patternBind) {
		assert false: "INVESTIGATE PATTERN BIND"; 
		return null;
//		assert (patternBind.getDefs() != null) :
//			"PatternBind must be type checked before getDefinitions";
//
//		return patternBind.getDefs();
	}
	
}
