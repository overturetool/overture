package org.overture.ast.modules.assistant;

import org.overture.ast.modules.AModuleModules;

public class AModuleModulesAssistante
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

}
