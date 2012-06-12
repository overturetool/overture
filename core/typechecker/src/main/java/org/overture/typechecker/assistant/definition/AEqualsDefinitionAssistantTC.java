package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetBindAssistantTC;

public class AEqualsDefinitionAssistantTC {

	public static PDefinition findName(AEqualsDefinition d,
			LexNameToken sought, NameScope scope) {
		
		List<PDefinition> defs = d.getDefs();
		
		if (defs != null)
		{
			PDefinition def = PDefinitionListAssistantTC.findName(defs, sought, scope);

			if (def != null)
			{
				return def;
			}
		}
		return null;
	}

	public static void unusedCheck(AEqualsDefinition d) {
		
		if (d.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(d.getDefs());
		}
		
	}

	public static List<PDefinition> getDefinitions(AEqualsDefinition d) {
		
		return d.getDefs() == null ? new Vector<PDefinition>() : d.getDefs();
	}

	public static LexNameList getVariableNames(AEqualsDefinition d) {
		
		return d.getDefs() == null ? new LexNameList() : PDefinitionListAssistantTC.getVariableNames(d.getDefs());
	}

	public static PType getType(AEqualsDefinition def) {
		return def.getDefType() != null ? def.getDefType() : AstFactory.newAUnknownType(def.getLocation());
	}

	public static LexNameList getOldNames(AEqualsDefinition def) {
		
		LexNameList list = PExpAssistantTC.getOldNames(def.getTest());

		if (def.getSetbind() != null)
		{
			list.addAll(ASetBindAssistantTC.getOldNames(def.getSetbind()));
		}

		return list;
	}

	public static boolean equals(AEqualsDefinition def, Object other) {
		
		if (other instanceof AEqualsDefinition)
		{
			return def.toString().equals(other.toString());
		}
		
		return false;
		
	}

}
