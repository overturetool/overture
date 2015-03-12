package org.overturetool.cgisa.transformations;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.map.HashedMap;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overturetool.cgisa.IsaChecks;

public class Dependencies
{

	private IsaChecks isaUtils = new IsaChecks();

	public DirectedGraph<AFuncDeclCG, DefaultEdge> calDepsAsGraph(
			List<AFuncDeclCG> funcs) throws AnalysisException
	{
		DirectedGraph<AFuncDeclCG, DefaultEdge> r = new DefaultDirectedGraph<>(DefaultEdge.class);
		for (AFuncDeclCG func : funcs)
		{
			List<AFuncDeclCG> dependencies = findDependencies(func, funcs);
			if (!dependencies.isEmpty())
			{
				r.addVertex(func);
				for (AFuncDeclCG dep : dependencies)
				{
					r.addVertex(dep);
					r.addEdge(func, dep);
				}
			}
		}
		return r;
	}

	public Map<AFuncDeclCG, List<AFuncDeclCG>> calcDepsAsMap(
			List<AFuncDeclCG> funcs)
	{
		Map<AFuncDeclCG, List<AFuncDeclCG>> r = new HashedMap<>();
		for (AFuncDeclCG func : funcs)
		{
			try
			{
				List<AFuncDeclCG> dependencies = findDependencies(func, funcs);
				if (!dependencies.isEmpty())
				{
					r.put(func, dependencies);
				} else
				{
					r.put(func, new Vector<AFuncDeclCG>());
				}
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
		return r;
	}

	private List<AFuncDeclCG> findDependencies(AFuncDeclCG func,
			List<AFuncDeclCG> funcs) throws AnalysisException
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
