package org.overture.codegen.trans;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conv.StateDesignatorToExpCG;

public class AssignStmTransformation extends DepthFirstAnalysisAdaptor
{
	private StateDesignatorToExpCG converter;
	
	public AssignStmTransformation(List<AClassDeclCG> classes, TransAssistantCG transAssistant)
	{
		this.converter = new StateDesignatorToExpCG(classes, transAssistant);
	}
	
	@Override
	public void caseAAssignmentStmCG(AAssignmentStmCG node)
			throws AnalysisException
	{
		SStmCG newNode = null;
		
		if(node.getTarget() instanceof AMapSeqStateDesignatorCG)
		{
			AMapSeqStateDesignatorCG target = (AMapSeqStateDesignatorCG) node.getTarget();

			SExpCG col = target.getMapseq().apply(converter);
			SExpCG index = target.getExp();
			SExpCG value = node.getExp();

			AMapSeqUpdateStmCG mapSeqUpd = new AMapSeqUpdateStmCG();
			mapSeqUpd.setCol(col);
			mapSeqUpd.setIndex(index.clone());
			mapSeqUpd.setValue(value.clone());
			mapSeqUpd.setSourceNode(node.getSourceNode());
			mapSeqUpd.setTag(node.getTag());
			
			newNode = mapSeqUpd;

		}
		else
		{
			AAssignToExpStmCG assign = new AAssignToExpStmCG();
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
