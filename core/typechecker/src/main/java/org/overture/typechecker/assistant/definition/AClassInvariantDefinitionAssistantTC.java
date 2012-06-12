package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexNameList;

public class AClassInvariantDefinitionAssistantTC {

	public static List<PDefinition> getDefinitions(AClassInvariantDefinition d) {
		
		return new Vector<PDefinition>();
	}

	public static LexNameList getVariableNames(
			AClassInvariantDefinition d) {
		
		return new LexNameList(d.getName());
	}
	

}
