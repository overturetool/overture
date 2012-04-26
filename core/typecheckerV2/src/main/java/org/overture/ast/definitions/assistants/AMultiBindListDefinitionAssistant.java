package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmjV2.lex.LexNameList;
import org.overturetool.vdmjV2.lex.LexNameToken;
import org.overturetool.vdmjV2.typechecker.NameScope;

public class AMultiBindListDefinitionAssistant {

	public static PDefinition findName(AMultiBindListDefinition d,
			LexNameToken sought, NameScope scope) {
		
		if (d.getDefs() != null)
		{
			PDefinition def = PDefinitionListAssistant.findName(d.getDefs(), sought, scope);

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
			PDefinitionListAssistant.unusedCheck(d.getDefs());
		}
		
	}

	public static List<PDefinition> getDefinitions(AMultiBindListDefinition d) {
		
		return d.getDefs() == null ? new Vector<PDefinition>() : d.getDefs();
	}

	public static LexNameList getVariableNames(AMultiBindListDefinition d) {
		
		return d.getDefs() == null ? new LexNameList() :  PDefinitionListAssistant.getVariableNames(d.getDefs());
	}

	public static PType getType(AMultiBindListDefinition def) {
		if (def.getDefs() != null && def.getDefs().size() == 1)
		{
			return PDefinitionAssistantTC.getType(def.getDefs().get(0));
		}

		return new AUnknownType(def.getLocation(),false);
	}

}
