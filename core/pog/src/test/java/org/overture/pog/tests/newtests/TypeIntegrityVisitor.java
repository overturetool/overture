package org.overture.pog.tests.newtests;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ARecordInvariantType;

public class TypeIntegrityVisitor extends DepthFirstAnalysisAdaptor implements IntegrityCheck
{

	List<INode> untypeNodes = new Vector<INode>();

	@Override
	public void defaultInPExp(PExp node) throws AnalysisException
	{
		if (node.getType() == null)
		{
			untypeNodes.add(node);
		}
	}

	@Override
	public List<INode> getProblemNodes()
	{
		return untypeNodes;
	}


	public void reset()
	{
		untypeNodes.clear();
	}

	@Override
	public void caseAEqualsDefinition(AEqualsDefinition node)
			throws AnalysisException
	{
		if (node.getExpType() == null)
		{
			untypeNodes.add(node);
		}
	}

	@Override
	public void caseAFunctionType(AFunctionType node) throws AnalysisException
	{
		// do nothing
	}

	@Override
	public void caseARecordInvariantType(ARecordInvariantType node)
			throws AnalysisException
	{
		// do nothing
	}

}
