package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;

public class APerSyncDefinitionAssistantTC {

	public static PDefinition findName(APerSyncDefinition d,
			ILexNameToken sought, NameScope scope) {
		
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
