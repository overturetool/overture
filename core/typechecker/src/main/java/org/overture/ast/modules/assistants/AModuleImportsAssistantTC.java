package org.overture.ast.modules.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.ModuleEnvironment;
import org.overture.typecheck.TypeCheckerErrors;

public class AModuleImportsAssistantTC {

	public static List<PDefinition> getDefinitions(
			AModuleImports imports, List<AModuleModules> allModules) {
		List<PDefinition> defs = new Vector<PDefinition>();

		for (AFromModuleImports ifm: imports.getImports())
		{
			if (ifm.getName().name.equals(imports.getName()))
			{
				TypeCheckerErrors.report(3195, "Cannot import from self", ifm.getName().location, ifm);
				continue;
			}

			AModuleModules from = AModuleModulesAssistantTC.findModule(allModules,ifm.getName());

			if (from == null)
			{
				TypeCheckerErrors.report(3196, "No such module as " + ifm.getName(), ifm.getName().location,ifm);
			}
			else
			{
				defs.addAll(AFromModuleImportsAssistantTC.getDefinitions(ifm,from));
			}
		}

		return defs;
	}

	public static void typeCheck(AModuleImports imports,
			ModuleEnvironment env) {
		
		for (AFromModuleImports ifm: imports.getImports())
		{
			AFromModuleImportsAssistantTC.typeCheck(ifm,env);
		}
		
	}

}
