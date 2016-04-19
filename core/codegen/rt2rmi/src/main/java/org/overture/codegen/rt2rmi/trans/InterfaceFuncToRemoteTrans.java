package org.overture.codegen.rt2rmi.trans;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AInterfaceDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.expressions.AAnonymousClassExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AInterfaceTypeIR;

import java.util.LinkedList;

import org.overture.codegen.ir.IRInfo;

public class InterfaceFuncToRemoteTrans extends DepthFirstAnalysisAdaptor
{
	
	private String systemClassName;
	private IRInfo info;
	
	public InterfaceFuncToRemoteTrans(String systemClassName, IRInfo info) {
		this.systemClassName=systemClassName;
		this.info=info;
	}
	
//	@Override
//	public void caseAInterfaceTypeIR(AInterfaceTypeIR node) throws AnalysisException {
//		// TODO Auto-generated method stub
//		super.caseAInterfaceTypeIR(node);
//		
//		
//		if (node.getAncestor(AAnonymousClassExpIR.class)!= null){
//			AAnonymousClassExpIR par = node.getAncestor(AAnonymousClassExpIR.class);
//			
//			LinkedList<AMethodDeclIR> met = par.getMethods();
//			
//			for (AMethodDeclIR m:met){
//				AExternalTypeIR runtimeExpType = new AExternalTypeIR();
//				runtimeExpType.setName("RemoteException");
//				m.getRaises().add(runtimeExpType);
//				
//			}
//		}
//		
//	}
	
	
	@Override
	public void caseAInterfaceDeclIR(AInterfaceDeclIR node) throws AnalysisException {
		// TODO Auto-generated method stub
		super.caseAInterfaceDeclIR(node);
	}
}
