package org.overture.typechecker.assistant.module;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.TypeCheckerErrors;

public class AModuleImportsAssistantTC {

	public static List<PDefinition> getDefinitions(
			AModuleImports imports, List<AModuleModules> allModules) {
		List<PDefinition> defs = new Vector<PDefinition>();

		for (AFromModuleImports ifm: imports.getImports())
		{
			if (ifm.getName().getName().equals(imports.getName()))
			{
				TypeCheckerErrors.report(3195, "Cannot import from self", ifm.getName().getLocation(), ifm);
				continue;
			}

			AModuleModules from = AModuleModulesAssistantTC.findModule(allModules,ifm.getName());

			if (from == null)
			{
				TypeCheckerErrors.report(3196, "No such module as " + ifm.getName(), ifm.getName().getLocation(),ifm);
			}
			else
			{
				defs.addAll(AFromModuleImportsAssistantTC.getDefinitions(ifm,from));
			}
		}

		return defs;
	}

	public static void typeCheck(AModuleImports imports,
			ModuleEnvironment env) throws AnalysisException {
		
		for (AFromModuleImports ifm: imports.getImports())
		{
			AFromModuleImportsAssistantTC.typeCheck(ifm,env);
		}
		
	}

}
