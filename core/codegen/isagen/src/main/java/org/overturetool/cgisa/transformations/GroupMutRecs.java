package org.overturetool.cgisa.transformations;

import java.util.LinkedList;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.overture.cgisa.extast.analysis.DepthFirstAnalysisIsaAdaptor;
import org.overture.cgisa.extast.declarations.AIsaClassDeclCG;
import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.trans.ITotalTransformation;

public class GroupMutRecs extends DepthFirstAnalysisIsaAdaptor implements
		ITotalTransformation
{

	private AIsaClassDeclCG result = null;
	Dependencies depUtils;
	DirectedGraph<AFuncDeclCG, DefaultEdge> deps;

	public GroupMutRecs()
	{
		super();
		deps = new DefaultDirectedGraph<>(DefaultEdge.class);
		depUtils = new Dependencies();
	}

	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		// clone original class into extended class
		result = new AIsaClassDeclCG();
		result.setAbstract(node.getAbstract());
		result.setAccess(node.getAccess());
		result.setFields(node.getFields());
		result.setFunctions(node.getFunctions());
		result.setInnerClasses(node.getInnerClasses());
		result.setInterfaces(node.getInterfaces());
		result.setMethods(node.getMethods());
		result.setMutexSyncs(node.getMutexSyncs());
		result.setName(node.getName());
		result.setPackage(node.getPackage());
		result.setPerSyncs(node.getPerSyncs());
		result.setSourceNode(node.getSourceNode());
		result.setStatic(node.getStatic());
		result.setSuperName(node.getSuperName());
		result.setTag(node.getTag());
		result.setThread(node.getThread());
		result.setTraces(node.getTraces());
		result.setTypeDecls(node.getTypeDecls());

		// now compute the mr func groups
		calcDependencies(result.getFunctions());

	}

	private void calcDependencies(LinkedList<AFuncDeclCG> funcs)
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
				result.getMutrecfuncs().add(aux);
			}
		}
	}

	@Override
	public AIsaClassDeclCG getResult()
	{
		return result;
	}

}
