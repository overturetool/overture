package org.overture.codegen.trans;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG;
import org.overture.codegen.cgast.statements.AMapPutStmCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.conv.FieldDesignatorToExpCG;

public class AssignStmTransformation extends DepthFirstAnalysisAdaptor
{
	private FieldDesignatorToExpCG converter;
	
	public AssignStmTransformation(IRInfo info, List<AClassDeclCG> classes, TransAssistantCG transAssistant)
	{
		this.converter = new FieldDesignatorToExpCG(info, classes, transAssistant);
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
			ALocalAssignmentStmCG localAssign = new ALocalAssignmentStmCG();
			localAssign.setTarget(node.getTarget().apply(converter));
			localAssign.setExp(node.getExp().clone());
			localAssign.setSourceNode(node.getSourceNode());
			localAssign.setTag(node.getTag());
			
			newNode = localAssign;
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
