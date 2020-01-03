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
package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.RecursiveLoops;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SFunctionDefinitionAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public SFunctionDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<List<PDefinition>> getParamDefinitions(
			SFunctionDefinition node, AFunctionType type,
			List<List<PPattern>> paramPatternList, ILexLocation location)
	{
		List<List<PDefinition>> defList = new ArrayList<List<PDefinition>>(); // new Vector<DefinitionList>();
		AFunctionType ftype = type; // Start with the overall function
		Iterator<List<PPattern>> piter = paramPatternList.iterator();

		while (piter.hasNext())
		{
			List<PPattern> plist = piter.next();
			List<PDefinition> defs = new Vector<PDefinition>();
			List<PType> ptypes = ftype.getParameters();
			Iterator<PType> titer = ptypes.iterator();

			if (plist.size() != ptypes.size())
			{
				// This is a type/param mismatch, reported elsewhere. But we
				// have to create definitions to avoid a cascade of errors.

				PType unknown = AstFactory.newAUnknownType(location);

				for (PPattern p : plist)
				{
					defs.addAll(af.createPPatternAssistant(location.getModule()).getDefinitions(p, unknown, NameScope.LOCAL));

				}
			} else
			{
				for (PPattern p : plist)
				{
					defs.addAll(af.createPPatternAssistant(location.getModule()).getDefinitions(p, titer.next(), NameScope.LOCAL));
				}
			}

			defList.add(af.createPDefinitionAssistant().checkDuplicatePatterns(node, defs));

			if (ftype.getResult() instanceof AFunctionType) // else???
			{
				ftype = (AFunctionType) ftype.getResult();
			}
		}

		return defList;
	}

	public List<PDefinition> findModuleDefinitions(Stack<ILexNameToken> stack, List<AModuleModules> modules)
	{
		List<PDefinition> list = new Vector<PDefinition>();

		for (ILexNameToken name: stack)
		{
			list.add(findModuleDefinition(name, modules));
		}

		return list;
	}

	private PDefinition findModuleDefinition(ILexNameToken sought, List<AModuleModules> modules)
	{
		for (AModuleModules module: modules)
		{
			for (PDefinition def: module.getDefs())
			{
				if (def.getName() != null && def.getName().equals(sought))
				{
					return def;
				}
			}
		}

		return null;
	}

	public List<PDefinition> findClassDefinitions(Stack<ILexNameToken> stack, List<SClassDefinition> classes)
	{
		List<PDefinition> list = new Vector<PDefinition>();

		for (ILexNameToken name: stack)
		{
			list.add(findClassDefinition(name, classes));
		}

		return list;
	}

	private PDefinition findClassDefinition(ILexNameToken sought, List<SClassDefinition> classes)
	{
		for (SClassDefinition clazz: classes)
		{
			for (PDefinition def: clazz.getDefinitions())
			{
				if (def.getName() != null && def.getName().equals(sought))
				{
					return def;
				}
			}
		}

		return null;
	}

	public void typeCheckCycles(AApplyExp apply, PDefinition parent, PDefinition called)
	{
		List<List<PDefinition>> cycles = RecursiveLoops.getInstance().getCycles(parent.getName());

		if (cycles != null)
		{
			List<List<String>> cycleNames = new Vector<List<String>>();
			List<List<PDefinition>> recursiveCycles = new Vector<List<PDefinition>>();
			boolean mutuallyRecursive = false;

			for (List<PDefinition> cycle: cycles)
			{
				if (cycle.contains(called))		// The parent cycle involves this apply call
				{
					recursiveCycles.add(cycle);
					cycleNames.add(RecursiveLoops.getInstance().getCycleNames(cycle));
					mutuallyRecursive = mutuallyRecursive || cycle.size() > 2;	// eg. [f, g, f]
				}

				checkCycleMeasures(cycle);
			}

			if (parent instanceof AExplicitFunctionDefinition)
			{
				AExplicitFunctionDefinition def = (AExplicitFunctionDefinition)parent;
  				def.setRecursive(true);

  				if (def.getMeasure() == null)
  				{
  					if (mutuallyRecursive)
  					{
  						TypeChecker.warning(5013, "Mutually recursive cycle has no measure", def.getLocation());

  						for (List<String> cycleName: cycleNames)
  						{
  							TypeChecker.detail("Cycle", cycleName);
  						}
  					}
  					else
  					{
  						TypeChecker.warning(5012, "Recursive function has no measure", def.getLocation());
  					}
  				}
			}
			else if (parent instanceof AImplicitFunctionDefinition)
			{
				AImplicitFunctionDefinition def = (AImplicitFunctionDefinition)parent;
   				def.setRecursive(true);

  				if (def.getMeasure() == null)
  				{
  					if (mutuallyRecursive)
  					{
  						TypeChecker.warning(5013, "Mutually recursive cycle has no measure", def.getLocation());

  						for (List<String> cycleName: cycleNames)
  						{
  							TypeChecker.detail("Cycle", cycleName);
  						}
  					}
  					else
  					{
  						TypeChecker.warning(5012, "Recursive function has no measure", def.getLocation());
  					}
  				}
			}

			apply.setRecursiveCycles(recursiveCycles);
		}
	}

	private void checkCycleMeasures(List<PDefinition> cycle)
	{
		for (int i = 0; i < cycle.size()-2; i++)
		{
			PDefinition d1 = cycle.get(i);
			PDefinition d2 = cycle.get(i+1);
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			PType a = measureType(d1, sb1);
			PType b = measureType(d2, sb2);

			if (a != null && b != null && !a.equals(b))
			{
				TypeChecker.report(3364, "Recursive cycle measures return different types", d1.getLocation());
				TypeChecker.detail(sb1.toString(), a);
				TypeChecker.detail(sb2.toString(), b);
			}
		}
	}

	private PType measureType(PDefinition def, StringBuilder mname)
	{
		if (def instanceof AExplicitFunctionDefinition)
		{
			AExplicitFunctionDefinition expl = (AExplicitFunctionDefinition)def;
			if (expl.getMeasureName() != null) mname.append(expl.getMeasureName()); else mname.append(def.getName().toString());
			return expl.getMeasureDef() != null ? expl.getMeasureDef().getType().getResult() : null;
		}
		else if (def instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinition impl = (AImplicitFunctionDefinition)def;
			if (impl.getMeasureName() != null) mname.append(impl.getMeasureName()); else mname.append(def.getName().toString());
			return impl.getMeasureDef() != null ? impl.getMeasureDef().getType().getResult() : null;
		}
		else
		{
			return null;
		}
	}
}
