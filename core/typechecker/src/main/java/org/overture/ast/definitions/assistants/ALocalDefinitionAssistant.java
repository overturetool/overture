package org.overture.ast.definitions.assistants;

import java.util.Collection;

import org.overture.ast.definitions.ALocalDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;

public class ALocalDefinitionAssistant {

	

	public static Collection<? extends LexNameToken> getVariableNames(
			ALocalDefinition ld) {
		return new LexNameList(ld.getName());
	}

	public static void setValueDefinition(ALocalDefinition ld) {
		ld.setValueDefinition(true);
		
	}

	

}
