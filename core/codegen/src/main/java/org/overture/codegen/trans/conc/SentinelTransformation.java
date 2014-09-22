package org.overture.codegen.trans.conc;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRInfo;


public class SentinelTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private List<AClassDeclCG> classes;
	
	public SentinelTransformation(IRInfo info, List<AClassDeclCG> classes)
	{
		this.info = info;
		this.classes = classes;
	}

	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}
		
		if (node.getThread() != null)
		{
			makeThread(node);
		}
		
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
	
	private void makeThread(AClassDeclCG node)
	{
		AClassDeclCG threadClass = getThreadClass(node.getSuperName(), node);
		threadClass.setSuperName("Thread");
	}

	private AClassDeclCG getThreadClass(String superName, AClassDeclCG classCg)
	{
		if(superName == null)
		{
			return classCg;
		}
		else
		{
			AClassDeclCG superClass = null;
			
			for(AClassDeclCG c : classes)
			{
				if(c.getName().equals(superName))
				{
					superClass = c;
					break;
				}
			}
			
			return getThreadClass(superClass.getName(), superClass);
		}
	}
	
//	#set ( $baseclass = "" )
//	#if (!$JavaFormat.isNull($node.getThread()))
//		#set ( $baseclass = "extends Thread" )
//	#end
}
