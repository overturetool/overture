package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AMultiBindListDefinitionAssistant {

	public static PDefinition findName(AMultiBindListDefinition d,
			LexNameToken sought, NameScope scope) {
		
		if (d.getDefs() != null)
		{
			PDefinition def = PDefinitionAssistant.findName(d.getDefs(), sought, scope);

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
			PDefinitionAssistant.unusedCheck(d.getDefs());
		}
		
	}

}
