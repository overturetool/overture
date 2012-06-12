package org.overture.typechecker.assistant.definition;

import java.util.Iterator;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

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
		return PPatternAssistantTC.getVariableNames(d.getPattern());
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

	public static boolean equals(AValueDefinition def, Object other) {
		if (other instanceof AValueDefinition)
		{
			AValueDefinition vdo = (AValueDefinition)other;

			if (def.getDefs().size() == vdo.getDefs().size())
			{
				Iterator<PDefinition> diter = vdo.getDefs().iterator();

				for (PDefinition d: def.getDefs())
				{
					if (!PDefinitionAssistantTC.equals(diter.next(),d))
					{
						return false;
					}
				}

				return true;
			}
		}

		return false;
	}

	

}
