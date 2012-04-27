package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;

public class AClassInvariantDefinitionAssistant {

	public static List<PDefinition> getDefinitions(AClassInvariantDefinition d) {
		
		return new Vector<PDefinition>();
	}

	public static LexNameList getVariableNames(
			AClassInvariantDefinition d) {
		
		return new LexNameList(d.getName());
	}
	

}
