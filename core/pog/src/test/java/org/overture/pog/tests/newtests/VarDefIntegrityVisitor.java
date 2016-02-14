package org.overture.pog.tests.newtests;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;

public class VarDefIntegrityVisitor extends DepthFirstAnalysisAdaptor implements IntegrityCheck
{

	List<INode> problemNodes = new Vector<INode>();

	@Override
	public void inAVariableExp(AVariableExp node) throws AnalysisException
	{
		if (node.getVardef() == null)
		{
			problemNodes.add(node);
		}
	}

	@Override
	public List<INode> getProblemNodes()
	{
		return problemNodes;
	}

}
