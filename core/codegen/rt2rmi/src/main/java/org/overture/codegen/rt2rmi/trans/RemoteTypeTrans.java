package org.overture.codegen.rt2rmi.trans;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
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
	public void caseAClassTypeCG(AClassTypeCG node) throws AnalysisException {

		//Change the name to the interface name
		
		// Do not transform if the name is the system class
		if(node.getName().equals(systemClassName))
		{
			return;
		}
		
		// Do not transform if the name is attached to a new expression
		if(node.parent() instanceof ANewExpCG)
		{
			return;
		}
		
		// Do not transform if it is a library name
		if(info.getDeclAssistant().isLibraryName(node.getName()))
		{
			return;
		}
		
		node.setName(node.getName() + "_i");
	}
}
