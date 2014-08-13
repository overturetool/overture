package org.overture.typechecker.assistant.module;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AModuleModulesAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AModuleModulesAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	/**
	 * Generate the exportdefs list of definitions. The exports list of export declarations is processed by searching
	 * the defs list of locally defined objects. The exportdefs field is populated with the result.
	 * 
	 * @param m
	 */
	public void processExports(AModuleModules m)
	{
		if (m.getExports() != null)
		{
			if (!m.getIsDLModule())
			{
				m.getExportdefs().addAll(af.createAModuleExportsAssistant().getDefinitions(m.getExports(), m.getDefs()));
			} else
			{
				m.getExportdefs().addAll(af.createAModuleExportsAssistant().getDefinitions(m.getExports()));
			}
		}
	}

	public void processImports(AModuleModules m, List<AModuleModules> allModules)
	{

		if (m.getImports() != null)
		{
			List<PDefinition> updated = af.createAModuleImportsAssistant().getDefinitions(m.getImports(), allModules);

			D: for (PDefinition u : updated)
			{
				for (PDefinition tc : m.getImportdefs())
				{
					if (tc.getName() != null && u.getName() != null
							&& tc.getName().matches(u.getName()))
					{
						u.setUsed(tc.getUsed()); // Copy usage from TC phase
						continue D;
					}
				}
			}

			m.getImportdefs().clear();
			m.getImportdefs().addAll(updated);
		}

	}

	public AModuleModules findModule(List<AModuleModules> allModules,
			ILexIdentifierToken sought)
	{

		for (AModuleModules m : allModules)
		{
			if (m.getName().equals(sought))
			{
				return m;
			}
		}

		return null;
	}

	public void typeCheckImports(AModuleModules m) throws AnalysisException
	{
		if (m.getImports() != null)
		{
			af.createAModuleImportsAssistant().typeCheck(m.getImports(), new ModuleEnvironment(af, m));
		}

	}

}
