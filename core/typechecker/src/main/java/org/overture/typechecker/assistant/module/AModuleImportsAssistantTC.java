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
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AModuleImportsAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AModuleImportsAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<PDefinition> getDefinitions(AModuleImports imports,
			List<AModuleModules> allModules)
	{
		List<PDefinition> defs = new Vector<PDefinition>();

		for (AFromModuleImports ifm : imports.getImports())
		{
			if (ifm.getName().getName().equals(imports.getName()))
			{
				TypeCheckerErrors.report(3195, "Cannot import from self", ifm.getName().getLocation(), ifm);
				continue;
			}

			AModuleModules from = af.createAModuleModulesAssistant().findModule(allModules, ifm.getName());

			if (from == null)
			{
				TypeCheckerErrors.report(3196, "No such module as "
						+ ifm.getName(), ifm.getName().getLocation(), ifm);
			} else
			{
				defs.addAll(af.createAFromModuleImportsAssistant().getDefinitions(ifm, from));
			}
		}

		return defs;
	}

	public void typeCheck(AModuleImports imports, ModuleEnvironment env)
			throws AnalysisException
	{

		for (AFromModuleImports ifm : imports.getImports())
		{
			af.createAFromModuleImportsAssistant().typeCheck(ifm, env);
		}

	}

}
