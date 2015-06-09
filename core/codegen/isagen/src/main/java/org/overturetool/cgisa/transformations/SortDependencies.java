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
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;

public class SortDependencies extends DepthFirstAnalysisAdaptor
{
	List<AFuncDeclCG> funcs;
	Map<AFuncDeclCG, List<AFuncDeclCG>> depGraph;
	private List<AFuncDeclCG> sorted;

	protected Dependencies depUtils;

	public SortDependencies(LinkedList<SDeclCG> linkedList)
	{
		this.funcs = filterFuncs(linkedList);
		this.depUtils = new Dependencies();
		this.depGraph = new HashMap<>();
		this.sorted = new Vector<AFuncDeclCG>();
		init();
	}

	private List<AFuncDeclCG> filterFuncs(LinkedList<SDeclCG> linkedList)
	{
		List<AFuncDeclCG> r = new LinkedList<AFuncDeclCG>();

		for (SDeclCG d : linkedList)
		{
			if (d instanceof AFuncDeclCG)
			{
				r.add((AFuncDeclCG) d);
			}
		}
		return r;
	}

	@Override
	public void caseAModuleDeclCG(AModuleDeclCG node) throws AnalysisException
	{
		node.getDecls().removeAll(funcs);
		for (AFuncDeclCG f : sorted)
		{
			node.getDecls().add(f.clone());
		}
	}

	private void init()
	{
		this.depGraph = depUtils.calcDepsAsMap(funcs);
		sortDeps();
	}

	private void sortDeps()
	{
		Set<AFuncDeclCG> unmarked = depGraph.keySet();
		Set<AFuncDeclCG> tempMarks = new HashSet<>();

		while (!unmarked.isEmpty())
		{
			AFuncDeclCG n = unmarked.toArray(new AFuncDeclCG[1])[0];
			visit(n, tempMarks, unmarked);
		}
	}

	private void visit(AFuncDeclCG n, Set<AFuncDeclCG> tempMarks,
			Set<AFuncDeclCG> unmarked)
	{
		if (tempMarks.contains(n))
		{
			throw new RuntimeException("Cyclic dependency");
		}
		if (unmarked.contains(n))
		{
			tempMarks.add(n);

			for (AFuncDeclCG d : depGraph.get(n))
			{
				visit(d, tempMarks, unmarked);
			}
			unmarked.remove(n);
			tempMarks.remove(n);
			sorted.add(n); // we want reverse topological order since its dependencies.
		}
	}
}
