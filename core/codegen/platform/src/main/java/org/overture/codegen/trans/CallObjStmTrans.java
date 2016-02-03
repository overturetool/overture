package org.overture.codegen.trans;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ACallObjectExpStmCG;
import org.overture.codegen.ir.statements.ACallObjectStmCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpCG;

public class CallObjStmTrans extends DepthFirstAnalysisAdaptor
{
	private ObjectDesignatorToExpCG converter;
	
	public CallObjStmTrans(IRInfo info)
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
