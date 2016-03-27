package org.overture.codegen.rt2rmi.trans;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.IRInfo;

public class FunctionToRemoteTrans extends DepthFirstAnalysisAdaptor
{
	
	private String systemClassName;
	private IRInfo info;
	
	public FunctionToRemoteTrans(String systemClassName, IRInfo info) {
		this.systemClassName=systemClassName;
		this.info=info;
	}
	
	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException {
		
		if(node.getAncestor(ARecordDeclIR.class)!=null){
			return;
		}
		AExternalTypeIR runtimeExpType = new AExternalTypeIR();
		runtimeExpType.setName("RemoteException");
		node.getRaises().add(runtimeExpType);
		

	}
}
