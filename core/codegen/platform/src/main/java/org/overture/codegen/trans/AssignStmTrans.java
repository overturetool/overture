package org.overture.codegen.trans;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.statements.AMapSeqStateDesignatorIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.conv.StateDesignatorToExpIR;

public class AssignStmTrans extends DepthFirstAnalysisAdaptor
{
	private StateDesignatorToExpIR converter;
	
	public AssignStmTrans(TransAssistantIR transAssistant)
	{
		this.converter = new StateDesignatorToExpIR(transAssistant);
	}
	
	@Override
	public void caseAAssignmentStmIR(AAssignmentStmIR node)
			throws AnalysisException
	{
		SStmIR newNode;
		
		if(node.getTarget() instanceof AMapSeqStateDesignatorIR)
		{
			AMapSeqStateDesignatorIR target = (AMapSeqStateDesignatorIR) node.getTarget();

			SExpIR col = target.getMapseq().apply(converter);
			SExpIR index = target.getExp();
			SExpIR value = node.getExp();

			AMapSeqUpdateStmIR mapSeqUpd = new AMapSeqUpdateStmIR();
			mapSeqUpd.setCol(col);
			mapSeqUpd.setIndex(index.clone());
			mapSeqUpd.setValue(value.clone());
			mapSeqUpd.setSourceNode(node.getSourceNode());
			mapSeqUpd.setTag(node.getTag());
			
			newNode = mapSeqUpd;

		}
		else
		{
			AAssignToExpStmIR assign = new AAssignToExpStmIR();
			assign.setTarget(node.getTarget().apply(converter));
			assign.setExp(node.getExp().clone());
			assign.setSourceNode(node.getSourceNode());
			assign.setTag(node.getTag());
			
			newNode = assign;
		}
		
		if(node.parent() != null)
		{
			node.parent().replaceChild(node, newNode);
		}
		else
		{
			Logger.getLog().printErrorln("Could not find parent of " + node + " in " + "'" + this.getClass().getSimpleName() + "'" );
		}
	}
}
