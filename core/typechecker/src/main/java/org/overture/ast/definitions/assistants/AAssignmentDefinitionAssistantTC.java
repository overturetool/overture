package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;

public class AAssignmentDefinitionAssistantTC {

	public static List<PDefinition> getDefinitions(
			AAssignmentDefinition d) {
		
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(d);
		return res;
	}

	public static LexNameList getVariableNames(AAssignmentDefinition d) {
		
		return new LexNameList(d.getName());
	}

}
