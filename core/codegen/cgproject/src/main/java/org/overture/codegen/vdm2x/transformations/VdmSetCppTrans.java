package org.overture.codegen.vdm2x.transformations;

import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ADistConcatUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADistIntersectUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADistUnionUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEnumSetExpCG;
import org.overture.codegen.cgast.expressions.ASetDifferenceBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetIntersectBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetProperSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetSubsetBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASetUnionBinaryExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.codegen.vdm2x.ConstructionUtils;

public class VdmSetCppTrans extends DepthFirstAnalysisAdaptor {
	
	private BaseTransformationAssistant baseAssistant;
	
	public VdmSetCppTrans(BaseTransformationAssistant baseAss) {
		// TODO Auto-generated constructor stub
		System.out.println("Set transform");
		baseAssistant = baseAss;
	}
	
	@Override
	public void caseASetSetTypeCG(ASetSetTypeCG node) throws AnalysisException {
		// TODO Auto-generated method stub
		AClassTypeCG c = new AClassTypeCG();
		c.getTypes().add(node.getSetOf().clone());
		c.setName("vdm_set::Set");
		baseAssistant.replaceNodeWith(node, c);
	}
	
	@Override
	public void caseASetDifferenceBinaryExpCG(ASetDifferenceBinaryExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "difference", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getLeft());
		args.add(node.getRight());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
//	@Override
//	public void caseAEnumSetExpCG(AEnumSetExpCG node) throws AnalysisException {
//		// TODO Auto-generated method stub
//		//super.caseAEnumSetExpCG(node);
//		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "create_set", node.getType().clone());
//		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
//		args.add(node.clone());
//		n.setArgs(args);
//		//baseAssistant.replaceNodeWith(node, n);
//	}
//	
	@Override
	public void caseASetUnionBinaryExpCG(ASetUnionBinaryExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "union", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getLeft());
		args.add(node.getRight());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseASetIntersectBinaryExpCG(ASetIntersectBinaryExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "intersection", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getLeft());
		args.add(node.getRight());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseASetSubsetBinaryExpCG(ASetSubsetBinaryExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "is_subset", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getLeft());
		args.add(node.getRight());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	@Override
	public void caseASetProperSubsetBinaryExpCG(ASetProperSubsetBinaryExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "is_proper_subset", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getLeft());
		args.add(node.getRight());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	
	@Override
	public void caseADistConcatUnaryExpCG(ADistConcatUnaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "distributed_concat", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getExp());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseADistIntersectUnaryExpCG(ADistIntersectUnaryExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "distributed_intersection", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getExp());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseADistUnionUnaryExpCG(ADistUnionUnaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_set", "distributed_union", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getExp());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	
}
