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

package org.overture.typechecker;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.annotations.Annotation;
import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.typechecker.Pass;
import org.overture.ast.util.modules.CombinedDefaultModule;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.annotations.TCAnnotation;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.visitor.TypeCheckVisitor;

/**
 * A class to coordinate all module type checking processing.
 */

public class ModuleTypeChecker extends TypeChecker
{
	/** The list of modules to check. */
	private final List<AModuleModules> modules;

	/**
	 * Create a type checker with the list of modules passed. The warnings flag indicates whether warnings should be
	 * printed or just counted.
	 */

	public final ITypeCheckerAssistantFactory assistantFactory;

	/**
	 * VDM-only constructor. <b>NOT</b> for use by extensions.
	 * 
	 * @param modules
	 */
	public ModuleTypeChecker(List<AModuleModules> modules)
	{
		super();
		assistantFactory = new TypeCheckerAssistantFactory();
		this.modules = modules;
	}

	/**
	 * Perform the type checking for the set of modules. This is a complicated process.
	 * <p>
	 * First the module names are checked for uniqueness. Then each module generates its implicit definitions (eg. for
	 * pre_ and post_ functions). Then export definitions for each module are generated, and the import definitions
	 * linked to them. Next all the definition in the set of modules are type resolved by creating a list of all
	 * modules' definitions and calling their typeResolve methods. Then the type checking of the modules' definitions
	 * can proceed, covering the types, values and remaining definitions in that order. Next, the declared types of the
	 * imports for each module are compared with the (now determined) types of the exports. Finally the usage of the
	 * imports and definitions for each module are checked.
	 */

	@Override
	public void typeCheck()
	{
		// Check for module name duplication

		boolean nothing = true;
		boolean hasFlat = false;

		for (AModuleModules m1 : modules)
		{
			for (AModuleModules m2 : modules)
			{
				if (m1 != m2 && m1.getName().equals(m2.getName()))
				{
					TypeChecker.report(3429, "Module " + m1.getName()
							+ " duplicates " + m2.getName(), m1.getName().getLocation());
				}
			}

			if (m1.getIsFlat())
			{
				hasFlat = true;
			} else
			{
				if (hasFlat && Settings.release == Release.CLASSIC)
				{
					TypeChecker.report(3308, "Cannot mix modules and flat specifications", m1.getName().getLocation());
				}
			}

			if (!m1.getTypeChecked())
			{
				nothing = false;
			}
		}

		if (nothing)
		{
			return;
		}

		// Mark top level definitions of flat specifications as used
		new PDefinitionAssistantTC(new TypeCheckerAssistantFactory());

		for (AModuleModules module : modules)
		{
			if (module instanceof CombinedDefaultModule)
			{
				for (PDefinition definition : module.getDefs())
				{
					assistantFactory.createPDefinitionAssistant().markUsed(definition);
				}
			}
		}

		// Generate implicit definitions for pre_, post_, inv_ functions etc.

		for (AModuleModules m : modules)
		{
			if (!m.getTypeChecked())
			{
				Environment env = new ModuleEnvironment(assistantFactory, m);
				assistantFactory.createPDefinitionListAssistant().implicitDefinitions(m.getDefs(), env);
			}
		}

		// Exports have to be identified before imports can be processed.

		for (AModuleModules m : modules)
		{
			if (!m.getTypeChecked())
			{
				assistantFactory.getTypeComparator().setCurrentModule(m.getName().getName());
				assistantFactory.createAModuleModulesAssistant().processExports(m); // Populate exportDefs
			}
		}

		// Process the imports early because renamed imports create definitions
		// which can affect type resolution.

		for (AModuleModules m : modules)
		{
			if (!m.getTypeChecked())
			{
				assistantFactory.getTypeComparator().setCurrentModule(m.getName().getName());
				assistantFactory.createAModuleModulesAssistant().processImports(m, modules); // Populate importDefs
			}
		}

		// Create a list of all definitions from all modules, including
		// imports of renamed definitions.

		List<PDefinition> alldefs = new Vector<PDefinition>();
		List<PDefinition> checkDefs = new Vector<PDefinition>();

		for (AModuleModules m : modules)
		{
			for (PDefinition d : m.getImportdefs())
			{
				alldefs.add(d);
				if (!m.getTypeChecked())
				{
					checkDefs.add(d);
				}
			}
		}

		for (AModuleModules m : modules)
		{
			for (PDefinition d : m.getDefs())
			{
				alldefs.add(d);
				if (!m.getTypeChecked())
				{
					checkDefs.add(d);
				}
			}
		}

		// Attempt type resolution of unchecked definitions from all modules.
		Environment env = new FlatCheckedEnvironment(assistantFactory, alldefs, NameScope.NAMESANDSTATE);
		TypeCheckVisitor tc = new TypeCheckVisitor();
		for (PDefinition d : checkDefs)
		{
			try
			{
				assistantFactory.createPDefinitionAssistant().typeResolve(d, tc,
						new TypeCheckInfo(assistantFactory, env).newModule(d.getLocation().getModule()));
			} catch (TypeCheckException te)
			{
				report(3430, te.getMessage(), te.location);
				
				if (te.extras != null)
				{
					for (TypeCheckException e: te.extras)
					{
						report(3430, e.getMessage(), e.location);
					}
				}
			} catch (AnalysisException te)
			{
				report(3431, te.getMessage(), null);// FIXME: internal error
			}
		}
		
		// Initialise any annotations
		Annotation.init(TCAnnotation.class);

		for (AModuleModules m: modules)
		{
			for (PAnnotation annotation: m.getAnnotations())
			{
				if (annotation.getImpl() instanceof TCAnnotation)
				{
					TCAnnotation impl = (TCAnnotation)annotation.getImpl();
					impl.tcBefore(m, null);
				}
			}
		}

		// Proceed to type check all definitions, considering types, values
		// and remaining definitions, in that order.

		for (Pass pass : Pass.values())
		{
			for (AModuleModules m : modules)
			{
				if (!m.getTypeChecked())
				{
					assistantFactory.getTypeComparator().setCurrentModule(m.getName().getName());
					Environment e = new ModuleEnvironment(assistantFactory, m);

					for (PDefinition d : m.getDefs())
					{
						// System.out.println("Number of Defs: " + m.getDefs().size());
						// System.out.println("Def to typecheck: " + d.getName());
						if (d.getPass() == pass)
						{
							try
							{
								d.apply(tc, new TypeCheckInfo(assistantFactory, e, NameScope.NAMES).newModule(m.getName().getName()));
								// System.out.println();
							} catch (TypeCheckException te)
							{
								report(3431, te.getMessage(), te.location);
								
								if (te.extras != null)
								{
									for (TypeCheckException ex: te.extras)
									{
										report(3431, ex.getMessage(), ex.location);
									}
								}
							} catch (AnalysisException te)
							{
								report(3431, te.getMessage(), null);// FIXME: internal error
							}
						}
						// System.out.println("Number of Defs: " + m.getDefs().size());
					}
				}
			}
		}

		for (AModuleModules m: modules)
		{
			for (PAnnotation annotation: m.getAnnotations())
			{
				if (annotation.getImpl() instanceof TCAnnotation)
				{
					TCAnnotation impl = (TCAnnotation)annotation.getImpl();
					impl.tcAfter(m, null);
				}
			}
		}

		// Report any discrepancies between the final checked types of
		// definitions and their explicit imported types. Rebuild the import/export lists first.

		for (AModuleModules m : modules)
		{
			assistantFactory.getTypeComparator().setCurrentModule(m.getName().getName());
			assistantFactory.createAModuleModulesAssistant().processExports(m);
		}

		for (AModuleModules m : modules)
		{
			assistantFactory.getTypeComparator().setCurrentModule(m.getName().getName());
			assistantFactory.createAModuleModulesAssistant().processImports(m, modules);
		}

		for (AModuleModules m : modules)
		{
			if (!m.getTypeChecked())
			{
				assistantFactory.getTypeComparator().setCurrentModule(m.getName().getName());
				assistantFactory.createAModuleModulesAssistant().processImports(m, modules); // Re-populate importDefs

				try
				{
					assistantFactory.createAModuleModulesAssistant().typeCheckExports(m);
					assistantFactory.createAModuleModulesAssistant().typeCheckImports(m);
				}
				catch (TypeCheckException te)
				{
					report(3432, te.getMessage(), te.location);
					
					if (te.extras != null)
					{
						for (TypeCheckException e: te.extras)
						{
							report(3432, e.getMessage(), e.location);
						}
					}
				}
				catch (AnalysisException te)
				{
					report(3431, te.getMessage(), null);// FIXME: internal error
				}
			}
		}

		// Any names that have not been referenced or exported produce "unused"
		// warnings.

		for (AModuleModules m : modules)
		{
			if (!m.getTypeChecked())
			{
				assistantFactory.createPDefinitionListAssistant().unusedCheck(m.getImportdefs());
				assistantFactory.createPDefinitionListAssistant().unusedCheck(m.getDefs());
			}
		}

		// Check for inter-definition cyclic dependencies before initialization
    	cyclicDependencyCheck(checkDefs);
	}
}
