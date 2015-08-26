package org.overture.codegen.trans.cppseq;

import java.util.LinkedList;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ADistMergeUnaryExpCG;
import org.overture.codegen.cgast.expressions.ADomainResByBinaryExpCG;
import org.overture.codegen.cgast.expressions.ADomainResToBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapInverseUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapOverrideBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapRangeUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
import org.overture.codegen.cgast.expressions.AMapUnionBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARangeResByBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARangeResToBinaryExpCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.cgast.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class VdmMapCppTrans extends DepthFirstAnalysisAdaptor {
	
	private BaseTransformationAssistant baseAssistant;
	private TransAssistantCG trans;
	
	public VdmMapCppTrans(BaseTransformationAssistant baseAss) {
		// TODO Auto-generated constructor stub
		baseAssistant = baseAss;
	}
	
	@Override
	public void caseAMapDomainUnaryExpCG(AMapDomainUnaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		STypeCG a = node.getType();
		if(a instanceof AMapMapTypeCG)
		{
			AMapMapTypeCG am = (AMapMapTypeCG)a;
			
			AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_map", "domain", am.getFrom());
			LinkedList<SExpCG> args = new LinkedList<SExpCG>();
			args.add(node.getExp());
			n.setArgs(args);
			baseAssistant.replaceNodeWith(node, n);
		}
	}
	
	@Override
	public void caseADistMergeUnaryExpCG(ADistMergeUnaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_map", "distributed_merge", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getExp());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseAMapInverseUnaryExpCG(AMapInverseUnaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseAMapInverseUnaryExpCG(node);
	}
	
	@Override
	public void caseAMapOverrideBinaryExpCG(AMapOverrideBinaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseAMapOverrideBinaryExpCG(node);
	}
	
	@Override
	public void caseAMapRangeUnaryExpCG(AMapRangeUnaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		STypeCG a = node.getType();
		if(a instanceof AMapMapTypeCG)
		{
			AMapMapTypeCG am = (AMapMapTypeCG)a;
			
			AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_map", "range", am.getTo());
			LinkedList<SExpCG> args = new LinkedList<SExpCG>();
			args.add(node);
			n.setArgs(args);
			baseAssistant.replaceNodeWith(node, n);
		}
	}
	
	@Override
	public void caseAMapSeqGetExpCG(AMapSeqGetExpCG node)
			throws AnalysisException {
		AApplyExpCG n = ConstructionUtils.consUtilCall("vdm_map", "get", node.getType());
		LinkedList<SExpCG> args = new LinkedList<SExpCG>();
		args.add(node.getCol());
		args.add(node.getIndex());
		n.setArgs(args);
		baseAssistant.replaceNodeWith(node, n);
	}
	
	@Override
	public void caseAMapSeqStateDesignatorCG(AMapSeqStateDesignatorCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseAMapSeqStateDesignatorCG(node);
	}
	
	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseAMapSeqUpdateStmCG(node);
	}
	
	@Override
	public void caseAMapUnionBinaryExpCG(AMapUnionBinaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseAMapUnionBinaryExpCG(node);
	}
	
	@Override
	public void caseADomainResByBinaryExpCG(ADomainResByBinaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseADomainResByBinaryExpCG(node);
	}
	
	@Override
	public void caseADomainResToBinaryExpCG(ADomainResToBinaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseADomainResToBinaryExpCG(node);
	}
	
	@Override
	public void caseARangeResByBinaryExpCG(ARangeResByBinaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseARangeResByBinaryExpCG(node);
	}
	
	@Override
	public void caseARangeResToBinaryExpCG(ARangeResToBinaryExpCG node)
			throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseARangeResToBinaryExpCG(node);
	}

}
