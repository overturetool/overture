package org.overture.codegen.trans.letexps;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class IfExpTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;

	public IfExpTransformation(BaseTransformationAssistant baseAssistant)
	{
		this.baseAssistant = baseAssistant;
	}

	@Override
	public void inATernaryIfExpCG(ATernaryIfExpCG node)
			throws AnalysisException
	{
		INode parent = node.parent();
		
		if(parent instanceof AReturnStmCG)
		{
			AIfStmCG ifStm = new AIfStmCG();
			ifStm.setSourceNode(node.getSourceNode());
			
			ifStm.setIfExp(node.getCondition().clone());
			
			AReturnStmCG thenStm = new AReturnStmCG();
			thenStm.setExp(node.getTrueValue().clone());
			ifStm.setThenStm(thenStm);

			AReturnStmCG elseStm = new AReturnStmCG();
			elseStm.setExp(node.getFalseValue().clone());
			ifStm.setElseStm(elseStm);
			
			baseAssistant.replaceNodeWithRecursively(parent, ifStm, this);
		}
	}
}
