package org.overture.ast.modules.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PAccessSpecifierAssistant;
import org.overture.ast.modules.AAllImport;
import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.TypeCheckerErrors;

public class AAllImportAssistant {

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
			imported.add(new AImportedDefinition(imp.getLocation(), d.getName(), d.getNameScope(), false, null, PAccessSpecifierAssistant.getDefault(), null, d));
		}

		return imported;	// The lot!
	}

}
