package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;


public class ALocalDefinitionAssistant {

	

	public static LexNameList getVariableNames(
			ALocalDefinition ld) {
		return new LexNameList(ld.getName());
	}

	public static void setValueDefinition(ALocalDefinition ld) {
		ld.setValueDefinition(true);
		
	}

	public static List<PDefinition> getDefinitions(ALocalDefinition d) {
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(d);
		return res;
	}

	

}
