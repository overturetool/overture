package org.overture.ast.modules.assistant;

import java.util.List;

import org.overture.ast.modules.AModuleModules;
import org.overturetool.vdmj.lex.LexIdentifierToken;

public class AModuleModulesAssistant
{
	/**
	 * Generate the exportdefs list of definitions. The exports list of
	 * export declarations is processed by searching the defs list of
	 * locally defined objects. The exportdefs field is populated with
	 * the result.
	 */
	public static void processExports(AModuleModules m)
	{
		if (m.getExports() != null)
		{
			m.getExportdefs().addAll(AModuleExportsAssistant.getDefinitions(m.getExports(),m.getDefs()));
		}
	}

	public static void processImports(AModuleModules m,
			List<AModuleModules> allModules) {
		
		if (m.getImports() != null)
		{
			m.getImportdefs().clear();
			m.getImportdefs().addAll( AModuleImportsAssistant.getDefinitions(m.getImports(), allModules));
		}
		
	}

	public static AModuleModules findModule(List<AModuleModules> allModules,
			LexIdentifierToken sought) {
		
		for (AModuleModules m: allModules)
		{
			if (m.getName().name.equals(sought))
			{
				return m;
			}
		}

   		return null;
	}

}
