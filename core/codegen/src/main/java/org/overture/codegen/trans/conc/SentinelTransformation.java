package org.overture.codegen.trans.conc;

import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRInfo;


public class SentinelTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	public SentinelTransformation(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}
		
		//boolean isInnerClass = node.getAncestor(AClassDeclCG.class) != null;
		
		AClassDeclCG innerClass = new AClassDeclCG();
		
		String classname = node.getName();
		LinkedList<AMethodDeclCG> innerClassMethods = (LinkedList<AMethodDeclCG>) node.getMethods().clone();
		
		innerClass.setName(classname+"_sentinel");
		innerClass.setMethods(innerClassMethods);
		
		if (node.getSuperName() != null){
			innerClass.setSuperName(node.getSuperName()+"_Sentinel");
		}
		else{
		
			innerClass.setSuperName("Sentinel");
		}
		innerClass.setAccess("public");
		
		node.getInnerClasses().add(innerClass);
		
	}
}
