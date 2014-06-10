package org.overture.pog.visitors;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;

public class StateDesignatorNameGetter extends AnswerAdaptor<String>
{

	@Override
	public String caseAIdentifierStateDesignator(AIdentifierStateDesignator node)
			throws AnalysisException
	{
		return node.getName().getFullName();
	}

	@Override
	public String caseAFieldStateDesignator(AFieldStateDesignator node)
			throws AnalysisException
	{
		return node.getField().getName() + node.getField();
	}

	@Override
	public String caseAMapSeqStateDesignator(AMapSeqStateDesignator node)
			throws AnalysisException
	{
		return node.getMapseq().apply(this) + node.getExp().toString();
	}

	@Override
	public String createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
