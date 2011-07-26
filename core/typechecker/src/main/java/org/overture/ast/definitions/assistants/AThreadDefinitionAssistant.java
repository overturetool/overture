package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.typecheck.Environment;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AThreadDefinitionAssistant {

	public static PDefinition findName(AThreadDefinition definition,
			LexNameToken sought, NameScope scope) {
		
		return PDefinitionAssistant.findName(definition.getOperationDef(),sought, scope);
	}

	public static List<PDefinition> getDefinitions(AThreadDefinition d) {
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d.getOperationDef());
		return result;
	}

	public static LexNameList getVariableNames(AThreadDefinition d) {
		return new LexNameList(d.getOperationDef().getName());
	}

	public static void implicitDefinitions(AThreadDefinition d, Environment env) {
		// TODO Auto-generated method stub
		
	}

}
