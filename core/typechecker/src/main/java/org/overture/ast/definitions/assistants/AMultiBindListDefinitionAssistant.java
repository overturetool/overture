package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

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

}
