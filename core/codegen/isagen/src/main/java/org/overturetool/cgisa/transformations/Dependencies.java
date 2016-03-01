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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.map.HashedMap;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overturetool.cgisa.IsaChecks;
import org.overture.codegen.ir.SDeclIR;

public class Dependencies
{

	private IsaChecks isaUtils = new IsaChecks();

	public DirectedGraph<AFuncDeclIR, DefaultEdge> calDepsAsGraph(
			List<AFuncDeclIR> decls) throws AnalysisException
	{
		DirectedGraph<AFuncDeclIR, DefaultEdge> r = new DefaultDirectedGraph<>(DefaultEdge.class);
		for (SDeclIR decl : decls)
		{
			if (decl instanceof AFuncDeclIR)
			{
				List<SDeclIR> dependencies = findDependencies(decl, decls);
				if (!dependencies.isEmpty())
				{
					r.addVertex((AFuncDeclIR) decl);
					for (SDeclIR dep : dependencies)
					{
						if (dep instanceof AFuncDeclIR)
						{
							r.addVertex((AFuncDeclIR) dep);
							r.addEdge((AFuncDeclIR) decl, (AFuncDeclIR) dep);
						}
					}
				}
			}
		}
		return r;
	}

	public Map<SDeclIR, List<SDeclIR>> calcDepsAsMap(List<SDeclIR> decls)
	{
		Map<SDeclIR, List<SDeclIR>> r = new HashedMap<>();
		for (SDeclIR decl : decls)
		{
			try
			{
				List<SDeclIR> dependencies = findDependencies(decl, decls);
				if (!dependencies.isEmpty())
				{
					r.put(decl, dependencies);
				} else
				{
					r.put(decl, new Vector<SDeclIR>());
				}
			} catch (AnalysisException e)
			{
				e.printStackTrace();
			}
		}
		return r;
	}

	private List<SDeclIR> findDependencies(SDeclIR decl,
			List<? extends SDeclIR> decls) throws AnalysisException
	{
		final Set<SVarExpIR> vars = new HashSet<SVarExpIR>();

		decl.apply(new DepthFirstAnalysisAdaptor()
		{
			public void defaultInSVarExpIR(SVarExpIR node)
					throws AnalysisException
			{
				if (isaUtils.isRoot(node) || isaUtils.isFieldRHS(node))
				{
					vars.add(node);
				}
			}
		});

		List<SDeclIR> deps = new Vector<SDeclIR>();
		for (SVarExpIR v : vars)
		{
			for (SDeclIR d : decls)
			{
				String n = "";
				if (d instanceof AFuncDeclIR)
				{
					n = ((AFuncDeclIR) d).getName();
				}

				else
				{
					if (d instanceof AFieldDeclIR)
					{
						n = ((AFieldDeclIR) d).getName();
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
