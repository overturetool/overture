package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmjV2.lex.LexNameList;
import org.overturetool.vdmjV2.lex.LexNameToken;
import org.overturetool.vdmjV2.typechecker.NameScope;

public class APerSyncDefinitionAssistant {

	public static PDefinition findName(APerSyncDefinition d,
			LexNameToken sought, NameScope scope) {
		
		return null;
	}

	public static List<PDefinition> getDefinitions(APerSyncDefinition d) {
		
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d);
		return result;
	}

	public static LexNameList getVariableNames(APerSyncDefinition d) {
		return new LexNameList();
	}

}
