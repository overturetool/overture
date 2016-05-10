/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.module;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PExport;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AModuleModulesAssistantTC implements IAstAssistant
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
				m.getExportdefs().addAll(getDefinitions(m.getExports(), m.getDefs()));
			} else
			{
				m.getExportdefs().addAll(getDefinitions(m.getExports()));
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
	
	public Collection<? extends PDefinition> getDefinitions(
			AModuleExports aModuleExports, LinkedList<PDefinition> actualDefs)
	{
		List<PDefinition> exportDefs = new ArrayList<PDefinition>();

		for (List<PExport> etype : aModuleExports.getExports())
		{
			for (PExport exp : etype)
			{
				exportDefs.addAll(getDefinition(exp, actualDefs));
			}
		}

		// Mark all exports as used

		for (PDefinition d : exportDefs)
		{
			af.createPDefinitionAssistant().markUsed(d);
		}

		return exportDefs;
	}

	public Collection<? extends PDefinition> getDefinitions(
			AModuleExports aModuleExports)
	{
		List<PDefinition> exportDefs = new ArrayList<PDefinition>();

		for (List<PExport> etype : aModuleExports.getExports())
		{
			for (PExport exp : etype)
			{
				exportDefs.addAll(getDefinition(exp));
			}
		}

		// Mark all exports as used

		for (PDefinition d : exportDefs)
		{
			af.createPDefinitionAssistant().markUsed(d);
		}

		return exportDefs;
	}
	
	public Collection<? extends PDefinition> getDefinition(PExport exp,
			LinkedList<PDefinition> actualDefs)
	{
		try
		{
			return exp.apply(af.getExportDefinitionFinder(), actualDefs);
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public Collection<? extends PDefinition> getDefinition(PExport exp)
	{
		try
		{
			return exp.apply(af.getExportDefinitionListFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
