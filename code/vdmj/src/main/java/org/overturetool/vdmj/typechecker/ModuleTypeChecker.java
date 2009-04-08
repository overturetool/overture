/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.typechecker;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;

/**
 * A class to coordinate all module type checking processing.
 */

public class ModuleTypeChecker extends TypeChecker
{
	/** The list of modules to check. */
	private final ModuleList modules;

	/**
	 * Create a type checker with the list of modules passed. The warnings
	 * flag indicates whether warnings should be printed or just counted.
	 *
	 * @param modules
	 */

	public ModuleTypeChecker(ModuleList modules)
	{
		super();
		this.modules = modules;
	}

	/**
	 * Perform the type checking for the set of modules. This is a complicated
	 * process.
	 * <p>
	 * First the module names are checked for uniqueness. Then each module
	 * generates its implicit definitions (eg. for pre_ and post_ functions).
	 * Then export definitions for each module are generated, and the import
	 * definitions linked to them. Next all the definition in the set of
	 * modules are type resolved by creating a list of all modules' definitions
	 * and calling their typeResolve methods. Then the type checking of the
	 * modules' definitions can proceed, covering the types, values and
	 * remaining definitions in that order. Next, the declared types of the
	 * imports for each module are compared with the (now determined) types of
	 * the exports. Finally the usage of the imports and definitions for each
	 * module are checked.
	 */

	@Override
	public void typeCheck()
	{
		// Check for module name duplication

		boolean nothing = true;

		for (Module m1: modules)
		{
			for (Module m2: modules)
			{
				if (m1 != m2 && m1.name.equals(m2.name))
				{
					TypeChecker.report(3429, "Module " + m1.name + " duplicate " + m2.name.location, m1.name.location);
				}
			}

			if (!m1.typechecked) nothing = false;
		}

		if (nothing)
		{
			return;
		}

		// Generate implicit definitions for pre_, post_, inv_ functions etc.

		for (Module m: modules)
		{
			if (!m.typechecked)
			{
				Environment env = new ModuleEnvironment(m);
				m.defs.implicitDefinitions(env);
			}
		}

		// Exports have to be identified before imports can be processed.

		for (Module m: modules)
		{
			if (!m.typechecked)
			{
				m.processExports();			// Populate exportDefs
			}
		}

		// Process the imports early because renamed imports create definitions
		// which can affect type resolution.

		for (Module m: modules)
		{
			if (!m.typechecked)
			{
				m.processImports(modules);	// Populate importDefs
			}
		}

		// Create a list of all definitions from all modules, including
		// imports of renamed definitions.

		DefinitionList alldefs = new DefinitionList();
		DefinitionList checkDefs = new DefinitionList();

		for (Module m: modules)
		{
			for (Definition d: m.importdefs)
			{
				alldefs.add(d);
				if (!m.typechecked) checkDefs.add(d);
			}

			for (Definition d: m.defs)
			{
				alldefs.add(d);
				if (!m.typechecked) checkDefs.add(d);
			}
		}

		// Attempt type resolution of unchecked definitions from all modules.

		Environment env = new FlatCheckedEnvironment(alldefs);

		for (Definition d: checkDefs)
		{
			try
			{
				d.typeResolve(env);
			}
			catch (TypeCheckException te)
			{
				report(3430, te.getMessage(), te.location);
			}
		}

		// Proceed to type check all definitions, considering types, values
		// and remaining definitions, in that order.

		for (Pass pass: Pass.values())
		{
			for (Module m: modules)
			{
				if (!m.typechecked)
				{
    				Environment e = new ModuleEnvironment(m);

    				for (Definition d: m.defs)
    				{
    					if (d.pass == pass)
    					{
    						try
    						{
    							d.typeCheck(e, NameScope.NAMES);
    						}
    						catch (TypeCheckException te)
    						{
    							report(3431, te.getMessage(), te.location);
    						}
    					}
    				}
				}
			}
		}

		// Report any discrepancies between the final checked types of
		// definitions and their explicit imported types.

		for (Module m: modules)
		{
			if (!m.typechecked)
			{
    			m.processImports(modules);		// Re-populate importDefs

    			try
    			{
    				m.typeCheckImports();		// Imports compared to exports
    			}
    			catch (TypeCheckException te)
    			{
    				report(3432, te.getMessage(), te.location);
    			}
			}
		}

		// Any names that have not been referenced or exported produce "unused"
		// warnings.

    	for (Module m: modules)
		{
			if (!m.typechecked)
			{
				m.importdefs.unusedCheck();
				m.defs.unusedCheck();
			}
		}
	}
}
