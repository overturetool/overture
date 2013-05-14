package org.overture.ide.plugins.codegen.visitor;

import java.util.HashMap;
import java.util.Set;

import org.overture.ide.plugins.codegen.nodes.ClassCG;

public class CodeGenContextMap
{
	private HashMap<String, AnalysisCG> analyses;
	
	public CodeGenContextMap()
	{
		analyses = new HashMap<String, AnalysisCG>();
	}

	public void registerCodeGenClass(ClassCG cgClass)
	{
		String className = cgClass.getClassName();
		CodeGenContext codeGenContex = new CodeGenContext();
		
		AnalysisCG analysis = new AnalysisCG(codeGenContex, cgClass);
		
		analyses.put(className, analysis);
	}
	
	public Set<String> getClassKeys()
	{
		return analyses.keySet();
	}
	
	public Set<String> getContextKeys()
	{
		return analyses.keySet();
	}
	
	public ClassCG getCodeGenClass(String className)
	{
		return analyses.get(className).getCodeGenClass();
	}
	
	public CodeGenContext getContext(String className)
	{
		return analyses.get(className).getCodeGenContext();
	}
		
	public void commit()
	{
		Set<String> classNames = getClassKeys();
		
		for (String name : classNames)
		{
			AnalysisCG analysis = analyses.get(name);
			
			ClassCG codeGenClass = analysis.getCodeGenClass();
			CodeGenContext codeGenContext = analysis.getCodeGenContext();
			
			codeGenClass.commit(codeGenContext);
		}
	}
}
