package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;
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

	public static List<PDefinition> getDefinitions(AInstanceVariableDefinition d) {
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(d);
		return res;
	}
	
	public static LexNameList getVariableNames(AInstanceVariableDefinition d) {
		return new LexNameList(d.getName());
	}

}
