package org.overture.ast.modules.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.TypeCheckerErrors;

public class AAllImportAssistantTC {

	public static List<PDefinition> getDefinitions(AAllImport imp,
			AModuleModules module) {
		imp.setFrom(module);

		if (imp.getFrom().getExportdefs().isEmpty())
		{
			TypeCheckerErrors.report(3190, "Import all from module with no exports?",imp.getLocation(),imp);
		} 

		List<PDefinition> imported = new Vector<PDefinition>() ;

		for (PDefinition d: imp.getFrom().getExportdefs())
 {
			PDefinition id = AstFactory.newAImportedDefinition(
					imp.getLocation(), d);
			PDefinitionAssistantTC.markUsed(id); // So imports all is quiet
			imported.add(id);

		}

		return imported;	// The lot!
	}

}
