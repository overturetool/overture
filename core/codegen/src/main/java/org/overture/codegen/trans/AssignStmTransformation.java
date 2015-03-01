package org.overture.codegen.trans;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.AMapPutStmCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conv.StateDesignatorToExpCG;

public class AssignStmTransformation extends DepthFirstAnalysisAdaptor
{
	private StateDesignatorToExpCG converter;
	
	public AssignStmTransformation(IRInfo info, List<AClassDeclCG> classes, TransAssistantCG transAssistant)
	{
		this.converter = new StateDesignatorToExpCG(info, classes, transAssistant);
	}
	
	@Override
	public void caseAAssignmentStmCG(AAssignmentStmCG node)
			throws AnalysisException
	{
		SStmCG newNode = null;
		if(node.getTarget() instanceof AMapSeqStateDesignatorCG)
		{
			AMapSeqStateDesignatorCG target = (AMapSeqStateDesignatorCG) node.getTarget();

			SExpCG mapExp = target.getMapseq().apply(converter);
			
			SExpCG domValue = target.getExp();
			SExpCG rngValue = node.getExp();

			AMapPutStmCG mapPut = new AMapPutStmCG();
			mapPut.setMap(mapExp);
			mapPut.setDomValue(domValue.clone());
			mapPut.setRngValue(rngValue.clone());
			mapPut.setSourceNode(node.getSourceNode());
			mapPut.setTag(node.getTag());
			
			newNode = mapPut;

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
