package org.overture.codegen.vdm2x;


import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqModificationBinaryExpCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class VdmSeqCppTrans extends DepthFirstAnalysisAdaptor {
	
		private BaseTransformationAssistant baseAssistant;
		private TransAssistantCG trans;

		public VdmSeqCppTrans(BaseTransformationAssistant baseAssistant)
		{
			this.baseAssistant = baseAssistant;
		}

		@Override
		public void caseASeqConcatBinaryExpCG(ASeqConcatBinaryExpCG node)
			throws AnalysisException {
			
			// replace node with call to library operation TODO: refactor constants into another class 
			// so custom libraries can be added efficiently
			AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_seq", "concat", node.getType());
			LinkedList<SExpCG> args = new LinkedList<SExpCG>();
			args.add(node.getLeft());
			args.add(node.getRight());
			n.setArgs(args);
			baseAssistant.replaceNodeWith(node, n);
		}
		
		@Override
		public void caseASeqModificationBinaryExpCG(ASeqModificationBinaryExpCG node)
			throws AnalysisException {
			AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_seq", "mod", node.getType());
			LinkedList<SExpCG> args = new LinkedList<SExpCG>();
			args.add(node.getLeft());
			args.add(node.getRight());
			n.setArgs(args);
			baseAssistant.replaceNodeWith(node, n);
		}
		
		

}
