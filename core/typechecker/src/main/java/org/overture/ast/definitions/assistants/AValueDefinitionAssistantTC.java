package org.overture.ast.definitions.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.assistants.PExpAssistantTC;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AValueDefinitionAssistantTC {

	public static PDefinition findName(AValueDefinition d, LexNameToken sought,
			NameScope scope) {

		if (scope.matches(NameScope.NAMES))
		{
			return PDefinitionListAssistantTC.findName(d.getDefs(),sought, scope);
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
    			PDefinitionAssistantTC.unusedCheck(def);
    		}
		}
		
	}

	public static List<PDefinition> getDefinitions(AValueDefinition d) {
		return d.getDefs();
	}

	public static LexNameList getVariableNames(AValueDefinition d) {
		return PPatternAssistantTC.getAllVariableNames(d.getPattern());
	}

	public static void typeResolve(AValueDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		 //d.setType(getType(d));
		if(d.getType() != null)
			d.setType(PTypeAssistantTC.typeResolve(d.getType(),null,rootVisitor,question));
		
		
	}

	public static PType getType(AValueDefinition def) {
		return def.getType() != null ? def.getType() :
			(def.getExpType() != null ? def.getExpType() : AstFactory.newAUnknownType(def.getLocation()));
	}

	public static LexNameList getOldNames(AValueDefinition def) {
		return PExpAssistantTC.getOldNames(def.getExpression());
	}

	

}
