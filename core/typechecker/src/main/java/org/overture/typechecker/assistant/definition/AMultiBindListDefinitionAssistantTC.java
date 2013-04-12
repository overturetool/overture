package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;

public class AMultiBindListDefinitionAssistantTC {

	public static PDefinition findName(AMultiBindListDefinition d,
			ILexNameToken sought, NameScope scope) {
		
		if (d.getDefs() != null)
		{
			PDefinition def = PDefinitionListAssistantTC.findName(d.getDefs(), sought, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static void unusedCheck(AMultiBindListDefinition d) {
		if (d.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(d.getDefs());
		}
		
	}

	public static List<PDefinition> getDefinitions(AMultiBindListDefinition d) {
		
		return d.getDefs() == null ? new Vector<PDefinition>() : d.getDefs();
	}

	public static LexNameList getVariableNames(AMultiBindListDefinition d) {
		
		return d.getDefs() == null ? new LexNameList() :  PDefinitionListAssistantTC.getVariableNames(d.getDefs());
	}

	public static PType getType(AMultiBindListDefinition def) {
		PTypeList types = new PTypeList();

		for (PDefinition definition: def.getDefs())
		{
			types.add(definition.getType());
		}

		AUnionType result = AstFactory.newAUnionType(def.getLocation(),types);		
		
		return result;
	}

	public static boolean equals(AMultiBindListDefinition def, Object other) {
		if (other instanceof AMultiBindListDefinition)
		{
			return def.toString().equals(other.toString());
		}

		return false;
	}

}
