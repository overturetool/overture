package org.overture.ast.modules.assistants;

import java.util.List;

import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ModuleEnvironment;
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
			if (!m.getIsDLModule())
				m.getExportdefs().addAll(AModuleExportsAssistant.getDefinitions(m.getExports(),m.getDefs()));
			else
				m.getExportdefs().addAll(AModuleExportsAssistant.getDefinitions(m.getExports()));
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
			if (m.getName().equals(sought))
			{
				return m;
			}
		}

   		return null;
	}

	public static void typeCheckImports(AModuleModules m) {
		if (m.getImports() != null)
		{
			AModuleImportsAssistant.typeCheck(m.getImports(),new ModuleEnvironment(m));	
		}
		
	}

}
