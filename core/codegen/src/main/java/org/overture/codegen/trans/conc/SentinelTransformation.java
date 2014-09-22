package org.overture.codegen.trans.conc;

import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;


public class SentinelTransformation extends DepthFirstAnalysisAdaptor
{
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
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
		
	}
}
