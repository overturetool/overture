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
	protected ITypeCheckerAssistantFactory af;

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
