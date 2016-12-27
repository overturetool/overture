package org.overture.rename;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;

public class ApplyAndCallDetector extends DepthFirstAnalysisAdaptor {
	
	private String[] parameters;
	private ILexLocation foundParentLocation;

	ApplyAndCallDetector(String[] param){
		parameters = param;
	}
	
	@Override
	public void caseAApplyExp(AApplyExp node) throws AnalysisException {
		checkLocation(node.getLocation(),node.getType().getLocation());
		super.caseAApplyExp(node);
	}
	
	@Override
	public void caseAApplyObjectDesignator(AApplyObjectDesignator node) throws AnalysisException {
		//checkLocation(node.getLocation(),node.get.getType().getLocation());
		super.caseAApplyObjectDesignator(node);
	}
	
	@Override
	public void caseACallStm(ACallStm node) throws AnalysisException {
		checkLocation(node.getLocation(),node.getType().getLocation());
		super.caseACallStm(node);
	}
	
	@Override
	public void caseACallObjectStm(ACallObjectStm node) throws AnalysisException {
		checkLocation(node.getLocation(),node.getType().getLocation());
		super.caseACallObjectStm(node);
	}
	
	private boolean checkLocation(ILexLocation loc, ILexLocation parentLocation){
		
		if(foundParentLocation == null){
			if(RenameUtil.compareNodeLocation(loc, parameters)){
				foundParentLocation = parentLocation;
				return true;
			}
		}
		return false;
	}
	
	public ILexLocation getFoundParentLocation() {
		return foundParentLocation;
	}

}
