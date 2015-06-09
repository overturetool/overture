package org.overturetool.cgisa.transformations;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.overture.cgisa.extast.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclCG;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.trans.ITotalTransformation;

public class GroupMutRecs extends DepthFirstAnalysisIsaAdaptor implements
		ITotalTransformation
{

	private AModuleDeclCG result = null;
	Dependencies depUtils;
	DirectedGraph<AFuncDeclCG, DefaultEdge> deps;
	List<AFuncDeclCG> funcs;

	public GroupMutRecs()
	{
		super();
		deps = new DefaultDirectedGraph<>(DefaultEdge.class);
		depUtils = new Dependencies();
		funcs = new LinkedList<AFuncDeclCG>();
	}

	@Override
	public void caseAModuleDeclCG(AModuleDeclCG node) throws AnalysisException
	{
		result = new AModuleDeclCG();
		result.setExports(node.getExports());
		result.setImport(node.getImport());
		result.setIsDLModule(node.getIsDLModule());
		result.setIsFlat(node.getIsFlat());
		result.setMetaData(node.getMetaData());
		result.setName(node.getName());
		result.setSourceNode(node.getSourceNode());
		result.setTag(node.getTag());
		result.setDecls(node.getDecls());
		filterFunctions(node.getDecls());
		calcDependencies();

	}

	private void filterFunctions(LinkedList<SDeclCG> decls)
	{
		for (SDeclCG d : decls)
		{
			if (d instanceof AFuncDeclCG)
			{
				funcs.add((AFuncDeclCG) d);
			}
		}
	}

	private void calcDependencies()
	{
		try
		{
			this.deps = depUtils.calDepsAsGraph(funcs);
		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}
		groupDeps();

	}

	private void groupDeps()
	{
		StrongConnectivityInspector<AFuncDeclCG, DefaultEdge> visitor = new StrongConnectivityInspector<>(deps);
		for (Set<AFuncDeclCG> scs : visitor.stronglyConnectedSets())
		{
			if (scs.size() > 1)
			{
				AMrFuncGroupDeclCG aux = new AMrFuncGroupDeclCG();
				aux.setFuncs(new LinkedList<>(scs));
				// this line also removes the function from the functions block
				result.getDecls().add(aux);
			}
		}
	}

	@Override
	public AModuleDeclCG getResult()
	{
		return result;
	}

}
