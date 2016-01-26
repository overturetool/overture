package org.overture.codegen.rt2rmi.trans;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ASystemClassDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;

public class SystemClassTrans extends DepthFirstAnalysisAdaptor{
	
	public SystemClassTrans() {
		// TODO Auto-generated constructor stub
	}
	
//	@Override
//	public void caseACla
	
	@Override
	public void caseASystemClassDeclCG(ASystemClassDeclCG node) throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseASystemClassDeclCG(node);
		
		System.out.println("name is: " + node.getName());
		
	}

	
//	@Override
//	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException {
//		// TODO Auto-generated method stub
//		System.out.println("name is: " + node.getName());
//	}
	
	
//	@Override
//	public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node) throws AnalysisException {
//		// TODO Auto-generated method stub
//		ASystemClassDeclCG anc = node.getAncestor(ASystemClassDeclCG.class);
//		
//		if(anc.getName()!=null) System.out.println(anc.getName());
//	}
}
