package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AInstanceVariableDefinitionAssistant {

	public static PDefinition findName(AInstanceVariableDefinition d, LexNameToken sought,
			NameScope scope) {
		
		PDefinition found = PDefinitionAssistant.findNameBaseCase(d, sought, scope);
		if (found != null) return found;
		return scope.matches(NameScope.OLDSTATE) &&
				d.getOldname().equals(sought) ? d : null;
	}

}
