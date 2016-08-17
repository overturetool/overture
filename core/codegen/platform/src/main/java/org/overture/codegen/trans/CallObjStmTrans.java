package org.overture.codegen.trans;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.ACallObjectStmIR;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpIR;

public class CallObjStmTrans extends DepthFirstAnalysisAdaptor
{
	private ObjectDesignatorToExpIR converter;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public CallObjStmTrans(IRInfo info)
	{
		this.converter = new ObjectDesignatorToExpIR(info);
	}

	@Override
	public void caseACallObjectStmIR(ACallObjectStmIR node)
			throws AnalysisException
	{
		ACallObjectExpStmIR callObjExpStm = new ACallObjectExpStmIR();
		callObjExpStm.setArgs(node.getArgs());
		callObjExpStm.setObj(node.getDesignator().apply(converter));
		callObjExpStm.setFieldName(node.getFieldName());
		callObjExpStm.setSourceNode(node.getSourceNode());
		callObjExpStm.setTag(node.getTag());
		callObjExpStm.setType(node.getType());

		if (node.parent() != null)
		{
			node.parent().replaceChild(node, callObjExpStm);
		} else
		{
			log.error("Could not find parent of " + node);
		}
	}
}
