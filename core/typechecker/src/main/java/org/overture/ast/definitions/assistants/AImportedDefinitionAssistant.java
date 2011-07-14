package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AImportedDefinitionAssistant {

	public static PDefinition findType(AImportedDefinition d,
			LexNameToken sought, String fromModule) {
		// We can only find an import if it is being sought from the module that
		// imports it.

		if (fromModule != null && !d.getLocation().module.equals(fromModule))
		{
			return null;	// Someone else's import
		}

		
		PDefinition def = PDefinitionAssistant.findType(d.getDef(), sought, fromModule);

		if (def != null)
		{
			PDefinitionAssistant.markUsed(d);
		}

		return d;
	}

	public static PDefinition findName(AImportedDefinition d,
			LexNameToken sought, NameScope scope) {
		
		PDefinition def =  PDefinitionAssistant.findName(d.getDef(), sought, scope);

		if (def != null)
		{
			PDefinitionAssistant.markUsed(def);
		}

		return def;
	}

	public static void markUsed(AImportedDefinition d) {
		d.setUsed(true);
		PDefinitionAssistant.markUsed(d.getDef());
		
	}

	public static List<PDefinition> getDefinitions(AImportedDefinition d) {

		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d.getDef());
		return result;
	}

	public static LexNameList getVariableNames(AImportedDefinition d) {
		return PDefinitionAssistant.getVariableNames(d.getDef());
	}

}
