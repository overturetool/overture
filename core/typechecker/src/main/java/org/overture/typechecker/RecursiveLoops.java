/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.modules.AModuleModules;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.SFunctionDefinitionAssistantTC;
import org.overture.typechecker.utilities.expression.ApplyFinder;

/**
 * A class to hold recursive loop data, which is used to detect mutual recursion and
 * missing measure functions.
 */
public class RecursiveLoops
{
	private static RecursiveLoops INSTANCE = null;

	private static class Apply
	{
		public final AApplyExp apply;
		public final PDefinition calling;

		public Apply(AApplyExp apply, PDefinition calling)
		{
			this.apply = apply;
			this.calling = calling;
		}
	}

	private Map<PDefinition, List<Apply>> applymap = null;
	private Map<ILexNameToken, List<List<PDefinition>>> recursiveLoops = null;

	public static RecursiveLoops getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new RecursiveLoops();
		}

		return INSTANCE;
	}

	public void reset()
	{
		recursiveLoops = new HashMap<ILexNameToken, List<List<PDefinition>>>();
		applymap = new HashMap<PDefinition, List<Apply>>();
	}

	public void addApplyExp(PDefinition parent, AApplyExp apply, PDefinition calling)
	{
		if (calling instanceof AExplicitFunctionDefinition ||
			calling instanceof AImplicitFunctionDefinition)
		{
			if (!applymap.containsKey(parent))
			{
				applymap.put(parent, new Vector<Apply>());
			}

			applymap.get(parent).add(new Apply(apply, calling));
		}
	}

	private Map<ILexNameToken, Set<ILexNameToken>> getCallMap()
	{
		Map<ILexNameToken, Set<ILexNameToken>> callmap = new HashMap<ILexNameToken, Set<ILexNameToken>>();
		ApplyFinder finder = new ApplyFinder();

		for (PDefinition def: applymap.keySet())
		{
			try
			{
				callmap.put(def.getName(), def.apply(finder));
			}
			catch (AnalysisException e)
			{
				// doesn't happen
			}
		}

		return callmap;
	}

	public void typeCheckClasses(List<SClassDefinition> classes, ITypeCheckerAssistantFactory af)
	{
		Map<ILexNameToken, Set<ILexNameToken>> callmap = getCallMap();
		recursiveLoops.clear();
		SFunctionDefinitionAssistantTC assistant = af.createSFunctionDefinitionAssistant();

		for (ILexNameToken name: callmap.keySet())
		{
			for (Stack<ILexNameToken> cycle: reachable(name, callmap))
			{
				addCycle(name, assistant.findClassDefinitions(cycle, classes));
			}
		}

		for (PDefinition parent: applymap.keySet())
		{
			for (Apply pair: applymap.get(parent))
			{
				assistant.typeCheckCycles(pair.apply, parent, pair.calling);
			}
		}

		reset();	// save space!
	}

	public void typeCheckModules(List<AModuleModules> modules, ITypeCheckerAssistantFactory af)
	{
		Map<ILexNameToken, Set<ILexNameToken>> callmap = getCallMap();
		recursiveLoops.clear();
		SFunctionDefinitionAssistantTC assistant = af.createSFunctionDefinitionAssistant();

		for (ILexNameToken name: callmap.keySet())
		{
			for (Stack<ILexNameToken> cycle: reachable(name, callmap))
			{
				addCycle(name, assistant.findModuleDefinitions(cycle, modules));
			}
		}

		for (PDefinition parent: applymap.keySet())
		{
			for (Apply pair: applymap.get(parent))
			{
				assistant.typeCheckCycles(pair.apply, parent, pair.calling);
			}
		}

		reset();	// save space!
	}

	private void addCycle(ILexNameToken name, List<PDefinition> defs)
	{
		List<List<PDefinition>> existing = getCycles(name);

		if (existing == null)
		{
			List<List<PDefinition>> list = new Vector<List<PDefinition>>();
			list.add(defs);
			recursiveLoops.put(name, list);
		}
		else
		{
			existing.add(defs);
		}
	}

	public List<List<PDefinition>> getCycles(ILexNameToken name)
	{
		return recursiveLoops.get(name);
	}

	public List<String> getCycleNames(List<PDefinition> cycle)
	{
		List<String> calls = new Vector<String>();

		for (PDefinition d: cycle)
		{
			calls.add(d.getName().toString());	// ie. include PP param types
		}

		return calls;
	}

	/**
	 * Return true if the name sought is reachable via the next set of names passed using
	 * the dependency map. The stack passed records the path taken to find a cycle.
	 */
	private Set<Stack<ILexNameToken>> reachable(ILexNameToken name,
			Map<ILexNameToken, Set<ILexNameToken>> callmap)
	{
		Stack<ILexNameToken> stack = new Stack<ILexNameToken>();
		Set<Stack<ILexNameToken>> loops = new HashSet<Stack<ILexNameToken>>();
		stack.push(name);

		reachable(name, callmap.get(name), callmap, stack, loops);

		return loops;
	}

	private boolean reachable(ILexNameToken sought, Set<ILexNameToken> nextset,
		Map<ILexNameToken, Set<ILexNameToken>> dependencies, Stack<ILexNameToken> stack,
		Set<Stack<ILexNameToken>> loops)
	{
		if (nextset == null)
		{
			return false;
		}

		if (nextset.contains(sought))
		{
			stack.push(sought);
			Stack<ILexNameToken> loop = new Stack<ILexNameToken>();
			loop.addAll(stack);
			loops.add(loop);
			return true;
		}

		if (System.getProperty("skip.recursion.check") != null)
		{
			return false;		// For now, to allow us to skip if there are issues.
		}

		boolean found = false;

		for (ILexNameToken nextname: nextset)
		{
			if (stack.contains(nextname))	// Been here before!
			{
				return false;
			}

			stack.push(nextname);

			if (reachable(sought, dependencies.get(nextname), dependencies, stack, loops))
			{
				Stack<ILexNameToken> loop = new Stack<ILexNameToken>();
				loop.addAll(stack);
				loops.add(loop);

				while (!stack.peek().equals(nextname))
				{
					stack.pop();
				}

				found = true;
			}

			stack.pop();
		}

		return found;
	}
}
