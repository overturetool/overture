package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AExternalDefinitionAssistant {

	public static PDefinition findName(AExternalDefinition d,
			LexNameToken sought, NameScope scope) {
		
		if (sought.old)
		{
			return (sought.equals(d.getOldname())) ? d : null;
		}

		return (sought.equals(d.getState().getName())) ? d : null;
	}

	public static void markUsed(AExternalDefinition d) {
		d.setUsed(true);
		PDefinitionAssistant.markUsed(d.getState());
		
	}

}
