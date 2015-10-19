package org.overture.codegen.vdm2java;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.IRInfo;

public class ImportsTrans extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	
	public ImportsTrans(IRInfo info)
	{
		this.info = info;
	}
	
	@Override
	public void caseADefaultClassDeclCG(ADefaultClassDeclCG node) throws AnalysisException
	{
		List<ClonableString> dep = new LinkedList<>();
		
		if(!info.getDeclAssistant().isInnerClass(node))
		{
			dep.add(new ClonableString("java.util.*"));
			dep.add(new ClonableString("org.overture.codegen.runtime.*"));
		}
		else if(!info.getDeclAssistant().isInnerClass(node) && isQuote(node))
		{
			dep.add(new ClonableString("org.overture.codegen.runtime.*"));
		}
		
		if(importTraceSupport(node))
		{
			dep.add(new ClonableString("org.overture.codegen.runtime.traces.*"));
		}
		
		node.setDependencies(dep);
	}
	
	public static boolean isQuote(ADefaultClassDeclCG classCg)
	{
		return classCg != null && "quotes".equals(classCg.getPackage());
	}
	
	public boolean importTraceSupport(ADefaultClassDeclCG node)
	{
		return info.getSettings().generateTraces() && !node.getTraces().isEmpty();
	}
}
