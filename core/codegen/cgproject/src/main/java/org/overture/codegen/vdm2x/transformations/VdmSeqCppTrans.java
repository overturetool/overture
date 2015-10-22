package org.overture.codegen.vdm2x.transformations;


import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AAssignExpExpCG;
import org.overture.codegen.cgast.expressions.AEnumSeqExpCG;
import org.overture.codegen.cgast.expressions.ASeqConcatBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqModificationBinaryExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AExternalTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.vdm2x.ConstructionUtils;

public class VdmSeqCppTrans extends DepthFirstAnalysisAdaptor {
	
		private BaseTransformationAssistant baseAssistant;
		private TransAssistantCG trans;

		public VdmSeqCppTrans(BaseTransformationAssistant baseAssistant)
		{
			this.baseAssistant = baseAssistant;
		}
		
		@Override
		public void caseASeqSeqTypeCG(ASeqSeqTypeCG node) throws AnalysisException {
		// TODO template type should have template arguments
		AClassTypeCG c = new AClassTypeCG();
		c.getTypes().add(node.getSeqOf().clone());
		c.setName("vdm_seq::Seq");
		baseAssistant.replaceNodeWith(node, c);
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
		
//		@Override
//		public void caseAEnumSeqExpCG(AEnumSeqExpCG node) throws AnalysisException {
//			//TODO optional to string transformation
//			AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_seq", "create_seq", node.getType());
//			LinkedList<SExpCG> args = new LinkedList<SExpCG>();
//			args.add(node.clone());
//			n.setArgs(args);
//			
//			baseAssistant.replaceNodeWith(node, n);
//		}
		
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
