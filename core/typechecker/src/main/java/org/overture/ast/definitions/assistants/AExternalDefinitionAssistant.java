package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameList;
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

	public static List<PDefinition> getDefinitions(AExternalDefinition d) {
		
		List<PDefinition> result =  new Vector<PDefinition>();
		result.add(d.getState());
	
		return result;
	}

	public static LexNameList getVariableNames(AExternalDefinition d) {
		return PDefinitionAssistant.getVariableNames(d.getState());
	}

	public static PType getType(AExternalDefinition def) {
		return PDefinitionAssistant.getType(def.getState());
	}

	public static boolean isUsed(AExternalDefinition u) {
		return PDefinitionAssistant.isUsed(u.getState());
		
	}

}
