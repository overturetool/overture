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
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overturetool.cgisa.IsaChecks;

public class SortDependencies extends DepthFirstAnalysisAdaptor
{
	List<AFuncDeclCG> funcs;
	Map<AFuncDeclCG, List<AFuncDeclCG>> depGraph;
	private List<AFuncDeclCG> sorted;

	protected IsaChecks isaUtils;

	public SortDependencies(List<AFuncDeclCG> funcs)
	{
		this.funcs = funcs;
		this.isaUtils = new IsaChecks();
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
		for (AFuncDeclCG func : funcs)
		{
			try
			{
				List<AFuncDeclCG> dependencies = findDependencies(func);
				if (!dependencies.isEmpty())
				{
					depGraph.put(func, dependencies);
				} else
				{
					depGraph.put(func, new Vector<AFuncDeclCG>());
				}
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}

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

	private List<AFuncDeclCG> findDependencies(AFuncDeclCG func)
			throws AnalysisException
	{
		final Set<SVarExpCG> vars = new HashSet<SVarExpCG>();

		func.getBody().apply(new DepthFirstAnalysisAdaptor()
		{
			public void defaultInSVarExpCG(SVarExpCG node)
					throws AnalysisException
			{
				if (isaUtils.isRoot(node))
				{
					vars.add(node);
				}
			}
		});

		List<AFuncDeclCG> deps = new Vector<AFuncDeclCG>();
		for (SVarExpCG v : vars)
		{
			for (AFuncDeclCG f : funcs)
			{
				if (v.getName().equals(f.getName()))
				{
					deps.add(f);
					break;
				}
			}
		}

		return deps;
	}
}
