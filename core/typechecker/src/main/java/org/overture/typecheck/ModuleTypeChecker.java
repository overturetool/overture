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

package org.overture.typecheck;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.assistant.AModuleModulesAssistante;
import org.overture.typecheck.visitors.TypeCheckVisitor;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.typechecker.NameScope;


/**
 * A class to coordinate all module type checking processing.
 */

public class ModuleTypeChecker extends TypeChecker
{
	/** The list of modules to check. */
	private final List<AModuleModules> modules;
	
//	private final List<AModuleModules> checkedModules = new Vector<AModuleModules>();

	/**
	 * Create a type checker with the list of modules passed. The warnings
	 * flag indicates whether warnings should be printed or just counted.
	 *
	 * @param modules
	 */

	public ModuleTypeChecker(List<AModuleModules> modules)
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
		boolean hasFlat = false;

		for (AModuleModules m1: modules)
		{
			for (AModuleModules m2: modules)
			{
				if (m1 != m2 && m1.getName().equals(m2.getName()))
				{
					TypeChecker.report(3429, "Module " + m1.getName() + " duplicates " + m2.getName(), m1.getName().location);
				}
			}
			
			if (m1.getIsFlat())
			{
				hasFlat = true;
			}
			else
			{
				if (hasFlat && Settings.release == Release.CLASSIC)
				{
					TypeChecker.report(3308, "Cannot mix modules and flat specifications", m1.getName().location);
				}
			}

//			if (!m1.gettypechecked) nothing = false;
		}

		if (nothing)
		{
			return;
		}

		// Generate implicit definitions for pre_, post_, inv_ functions etc.

		for (AModuleModules m: modules)
		{
//			if (!m.typechecked)
			{
				Environment env = new ModuleEnvironment(m);
				PDefinitionListAssistant.implicitDefinitions(m.getDefs(), env);
			}
		}

		// Exports have to be identified before imports can be processed.

		for (AModuleModules m: modules)
		{
//			if (!m.typechecked)
			{
				AModuleModulesAssistante.processExports(m);			// Populate exportDefs
			}
		}

		// Process the imports early because renamed imports create definitions
		// which can affect type resolution.

		for (AModuleModules m: modules)
		{
//			if (!m.typechecked)
			{
				//TODO
				//m.processImports(modules);	// Populate importDefs
			}
		}

		// Create a list of all definitions from all modules, including
		// imports of renamed definitions.

		List<PDefinition> alldefs = new Vector<PDefinition>();
		List<PDefinition> checkDefs = new Vector<PDefinition>();

		for (AModuleModules m: modules)
		{
			for (PDefinition d: m.getImportdefs())
			{
				alldefs.add(d);
				/*if (!m.typechecked)*/	checkDefs.add(d);
			}

			for (PDefinition d: m.getDefs())
			{
				alldefs.add(d);
				/*if (!m.typechecked)*/ checkDefs.add(d);
			}
		}

		// Attempt type resolution of unchecked definitions from all modules.

		Environment env =
			new FlatCheckedEnvironment(alldefs, NameScope.NAMESANDSTATE);
		TypeCheckVisitor tc = new TypeCheckVisitor();
		for (PDefinition d: checkDefs)
		{
			try
			{
//				d.typeResolve(env);
				//TODO???
				PDefinitionAssistant.typeResolve(d, tc, new TypeCheckInfo(env));
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
			for (AModuleModules m: modules)
			{
//				if (!m.typechecked)
				{
    				Environment e = new ModuleEnvironment(m);

    				for (PDefinition d: m.getDefs())
    				{
//    					if (d.pass == pass)//TODO we properly need to add this to all definitions
    					{
    						try
    						{
//    							d.typeCheck(e, NameScope.NAMES);
    							d.apply(tc,new TypeCheckInfo(e,NameScope.NAMES));
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

		for (AModuleModules m: modules)
		{
//			if (!m.typechecked)
			{
				//TODO
//    			m.processImports(modules);		// Re-populate importDefs

    			try
    			{
    				//TODO
//    				m.typeCheckImports();		// Imports compared to exports
    			}
    			catch (TypeCheckException te)
    			{
    				report(3432, te.getMessage(), te.location);
    			}
			}
		}

		// Any names that have not been referenced or exported produce "unused"
		// warnings.

    	for (AModuleModules m: modules)
		{
//			if (!m.typechecked)
			{
//				m.getImportdefs().unusedCheck();
				PDefinitionListAssistant.unusedCheck(m.getImportdefs());
				PDefinitionListAssistant.unusedCheck(m.getDefs());
//				m.getDefs().unusedCheck();
			}
		}
	}
}
