package org.overture.codegen.trans;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpCG;

public class CallObjStmTransformation extends DepthFirstAnalysisAdaptor
{
	private ObjectDesignatorToExpCG converter;
	
	public CallObjStmTransformation(IRInfo info)
	{
		this.converter = new ObjectDesignatorToExpCG(info); 
	}
	
	@Override
	public void caseACallObjectStmCG(ACallObjectStmCG node)
			throws AnalysisException
	{
		ACallObjectExpStmCG callObjExpStm = new ACallObjectExpStmCG();
		callObjExpStm.setArgs(node.getArgs());
		callObjExpStm.setObj(node.getDesignator().apply(converter));
		callObjExpStm.setFieldName(node.getFieldName());
		callObjExpStm.setSourceNode(node.getSourceNode());
		callObjExpStm.setTag(node.getTag());
		callObjExpStm.setType(node.getType());
		
		if(node.parent() != null)
		{
			node.parent().replaceChild(node, callObjExpStm);
		}
		else
		{
			Logger.getLog().printErrorln("Could not find parent of " + node + " in " + "'CallObjStmTransformation'");
		}
	}
}
