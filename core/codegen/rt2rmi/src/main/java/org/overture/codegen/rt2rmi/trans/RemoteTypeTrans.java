package org.overture.codegen.rt2rmi.trans;

import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.IRInfo;

public class RemoteTypeTrans extends DepthFirstAnalysisAdaptor
{
	
	private String systemClassName;
	private IRInfo info;
	
	public RemoteTypeTrans(String systemClassName, IRInfo info) {
		this.systemClassName=systemClassName;
		this.info=info;
	}
	
	@Override
	public void caseAClassTypeIR(AClassTypeIR node) throws AnalysisException {

		//Change the name to the interface name
		
		// Do not transform if the name is the system class
		if(node.getName().equals(systemClassName))
		{
			return;
		}
		
		if(node.getName().equals("Iterator"))
		{
			return;
		}
		
		// Do not transform if the name is attached to a new expression
		if(node.parent() instanceof ANewExpIR)
		{
			return;
		}
		
		// Do not transform if it is a library name
		if(info.getDeclAssistant().isLibraryName(node.getName()))
		{
			return;
		}
		
		// Do not transform variable references
		if(node.parent() instanceof AExplicitVarExpIR)
		{
			return;
		}
		
		if(node.getName().equals("bridge_FieldGraph")) 
			return;
		
		node.setName(node.getName() + "_i");
	}
}
