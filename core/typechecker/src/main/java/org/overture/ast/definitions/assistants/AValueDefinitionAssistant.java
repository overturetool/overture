package org.overture.ast.definitions.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AValueDefinitionAssistant {

	public static PDefinition findName(AValueDefinition d, LexNameToken sought,
			NameScope scope) {

		if (scope.matches(NameScope.NAMES))
		{
			return PDefinitionListAssistant.findName(d.getDefs(),sought, scope);
		}

		return null;
	}

	public static void unusedCheck(AValueDefinition d) {
		if (d.getUsed())	// Indicates all definitions exported (used)
		{
			return;
		}

		if (d.getDefs() != null)
		{
    		for (PDefinition def: d.getDefs())
    		{
    			PDefinitionAssistant.unusedCheck(def);
    		}
		}
		
	}

	public static List<PDefinition> getDefinitions(AValueDefinition d) {
		return d.getDefs();
	}

	public static LexNameList getVariableNames(AValueDefinition d) {
		return PPatternAssistant.getVariableNames(d.getPattern());
	}

	public static void typeResolve(AValueDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (d.getType() != null)
		{
			 d.setType(PTypeAssistant.typeResolve(d.getType(),null,rootVisitor,question));
		}
		
	}

	

}
