package org.overture.ast.modules.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;

public class AFromModuleImportsAssistant {

	public static List<PDefinition> getDefinitions(
			AFromModuleImports ifm, AModuleModules from) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		for (List<PImport> ofType: ifm.getSignatures())
		{
			for (PImport imp: ofType)
			{
				defs.addAll(PImportAssistant.getDefinitions(imp,from));
			}
		}

		return defs;
	}

}
