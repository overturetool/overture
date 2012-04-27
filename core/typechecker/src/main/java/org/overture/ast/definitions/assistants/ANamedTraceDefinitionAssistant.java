package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class ANamedTraceDefinitionAssistant {

	public static PDefinition findName(ANamedTraceDefinition d,
			LexNameToken sought, NameScope scope) {
		
		if (PDefinitionAssistantTC.findNameBaseCase(d, sought, scope) != null)
		{
			return d;
		}

		return null;
	}

	public static List<PDefinition> getDefinitions(ANamedTraceDefinition d) {

		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d);
		return result;
	}

	public static LexNameList getVariableNames(ANamedTraceDefinition d) {
		return new LexNameList();
	}

}
