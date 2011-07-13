package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AThreadDefinitionAssistant {

	public static PDefinition findName(AThreadDefinition definition,
			LexNameToken sought, NameScope scope) {
		
		return PDefinitionAssistant.findName(definition.getOperationDef(),sought, scope);
	}

}
