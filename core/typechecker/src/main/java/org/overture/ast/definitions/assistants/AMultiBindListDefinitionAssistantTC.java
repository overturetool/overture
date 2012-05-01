package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeList;
import org.overture.ast.types.assistants.PTypeSet;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AMultiBindListDefinitionAssistantTC {

	public static PDefinition findName(AMultiBindListDefinition d,
			LexNameToken sought, NameScope scope) {
		
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

		AUnionType result = new AUnionType(def.getLocation(), false, false, false);
		result.setTypes(types);
		
		return result;
	}

}
