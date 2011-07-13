package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class ANamedTraceDefinitionAssistant {

	public static PDefinition findName(ANamedTraceDefinition d,
			LexNameToken sought, NameScope scope) {
		
		if (PDefinitionAssistant.findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		return null;
	}

}
