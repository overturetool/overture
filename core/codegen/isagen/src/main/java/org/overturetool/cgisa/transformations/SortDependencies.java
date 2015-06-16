/*
 * #%~
 * The VDM to Isabelle Translator
 * %%
 * Copyright (C) 2008 - 2015 Overture
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
package org.overturetool.cgisa.transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;

public class SortDependencies extends DepthFirstAnalysisAdaptor
{
	List<SDeclCG> decls;
	Map<SDeclCG, List<SDeclCG>> depGraph;
	private List<SDeclCG> sorted;

	protected Dependencies depUtils;

	public SortDependencies(LinkedList<SDeclCG> linkedList)
	{
		this.depUtils = new Dependencies();
		this.depGraph = new HashMap<>();
		this.sorted = new Vector<SDeclCG>();
		this.decls = linkedList;
		init();
	}

	@Override
	public void caseAModuleDeclCG(AModuleDeclCG node) throws AnalysisException
	{
		node.getDecls().clear();
		for (SDeclCG d : sorted)
		{
			node.getDecls().add(d.clone());
		}
	}

	private void init()
	{
		this.depGraph = depUtils.calcDepsAsMap(decls);

		// add definitions w/no deps right away (to preserve order)
		for (SDeclCG d : decls)
		{
			if (depGraph.get(d).isEmpty())
			{
				sorted.add(d);
				depGraph.remove(d);
			}
		}
		sortDeps();
	}

	private void sortDeps()
	{
		Set<SDeclCG> unmarked = depGraph.keySet();
		Set<SDeclCG> tempMarks = new HashSet<>();

		while (!unmarked.isEmpty())
		{
			SDeclCG n = unmarked.toArray(new SDeclCG[1])[0];
			visit(n, tempMarks, unmarked);
		}
	}

	private void visit(SDeclCG n, Set<SDeclCG> tempMarks, Set<SDeclCG> unmarked)
	{
		if (tempMarks.contains(n))
		{
			throw new RuntimeException("Cyclic dependency");
		}
		if (unmarked.contains(n))
		{
			tempMarks.add(n);

			for (SDeclCG d : depGraph.get(n))
			{
				visit(d, tempMarks, unmarked);
			}
			unmarked.remove(n);
			tempMarks.remove(n);
			sorted.add(n); // we want reverse topological order since its dependencies.
		}
	}
}
