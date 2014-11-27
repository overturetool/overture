package org.overture.codegen.vdm2java.rt;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

// Here the DepthFirstAnalysisAdaptor for the codegen tree (IR) is applied
public class RemoteTypeTransformation extends DepthFirstAnalysisAdaptor{

	private String systemClassName;
	private IRInfo info;
	
	public RemoteTypeTransformation(String systemClassName, IRInfo info) {
		this.systemClassName = systemClassName;
		this.info = info;
	}

	@Override
	public void caseAClassTypeCG(AClassTypeCG node) throws AnalysisException {
		// TODO Auto-generated method stub
		//super.caseAClassTypeCG(node);	
		//Change the name to the interface name
		
		if(node.getName().equals(systemClassName))
		{
			return;
		}
		
		if(node.parent() instanceof ANewExpCG)
		{
			return;
		}
		
		if(info.getDeclAssistant().isLibraryName(node.getName()))
		{
			return;
		}
		
		node.setName(node.getName() + "_i");
	}
	
}
