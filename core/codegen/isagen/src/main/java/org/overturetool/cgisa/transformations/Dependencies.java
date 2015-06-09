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
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overturetool.cgisa.IsaChecks;
import org.overture.codegen.cgast.SDeclCG;

public class Dependencies
{

	private IsaChecks isaUtils = new IsaChecks();

	public DirectedGraph<AFuncDeclCG, DefaultEdge> calDepsAsGraph(
			List<AFuncDeclCG> decls) throws AnalysisException
	{
		DirectedGraph<AFuncDeclCG, DefaultEdge> r = new DefaultDirectedGraph<>(DefaultEdge.class);
		for (SDeclCG decl : decls)
		{
			if (decl instanceof AFuncDeclCG)
			{
				List<SDeclCG> dependencies = findDependencies(decl, decls);
				if (!dependencies.isEmpty())
				{
					r.addVertex((AFuncDeclCG) decl);
					for (SDeclCG dep : dependencies)
					{
						if (dep instanceof AFuncDeclCG)
						{
							r.addVertex((AFuncDeclCG) dep);
							r.addEdge((AFuncDeclCG) decl, (AFuncDeclCG) dep);
						}
					}
				}
			}
		}
		return r;
	}

	public Map<SDeclCG, List<SDeclCG>> calcDepsAsMap(List<SDeclCG> decls)
	{
		Map<SDeclCG, List<SDeclCG>> r = new HashedMap<>();
		for (SDeclCG decl : decls)
		{
			try
			{
				List<SDeclCG> dependencies = findDependencies(decl, decls);
				if (!dependencies.isEmpty())
				{
					r.put(decl, dependencies);
				} else
				{
					r.put(decl, new Vector<SDeclCG>());
				}
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
		return r;
	}

	private List<SDeclCG> findDependencies(SDeclCG decl,
			List<? extends SDeclCG> decls) throws AnalysisException
	{
		final Set<SVarExpCG> vars = new HashSet<SVarExpCG>();

		decl.apply(new DepthFirstAnalysisAdaptor()
		{
			public void defaultInSVarExpCG(SVarExpCG node)
					throws AnalysisException
			{
				if (isaUtils.isRoot(node) || isaUtils.isFieldRHS(node))
				{
					vars.add(node);
				}
			}
		});

		List<SDeclCG> deps = new Vector<SDeclCG>();
		for (SVarExpCG v : vars)
		{
			for (SDeclCG d : decls)
			{
				String n = "";
				if (d instanceof AFuncDeclCG)
				{
					n = ((AFuncDeclCG) d).getName();
				}

				else
				{
					if (d instanceof AFieldDeclCG)
					{
						n = ((AFieldDeclCG) d).getName();
					}
				}

				if (v.getName().equals(n))
				{
					deps.add(d);
					break;
				}
			}
		}

		return deps;
	}
}
