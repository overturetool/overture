package org.overturetool.cgisa.transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;

public class SortDependencies extends DepthFirstAnalysisAdaptor
{
	List<AFuncDeclCG> funcs;
	Map<AFuncDeclCG, List<AFuncDeclCG>> depGraph;
	private List<AFuncDeclCG> sorted;

	

	protected Dependencies depUtils;

	public SortDependencies(List<AFuncDeclCG> funcs)
	{
		this.funcs = funcs;
		this.depUtils = new Dependencies();
		this.depGraph = new HashMap<>();
		this.sorted = new Vector<AFuncDeclCG>();
		init();
	}

	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		List<AFuncDeclCG> clonedList = new Vector<AFuncDeclCG>();

		for (AFuncDeclCG f : sorted)
		{
			clonedList.add(f.clone());
		}
		node.setFunctions(clonedList);
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
