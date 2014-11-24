package org.overture.codegen.vdm2cpp;

import java.util.ArrayList;
import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

public class TypeHierachyAnalyser extends DepthFirstAnalysisAdaptor {
	
	private ArrayList<AClassDeclCG> class_list;
	
	public TypeHierachyAnalyser() {
		// TODO Auto-generated constructor stub
		class_list = new ArrayList<AClassDeclCG>();
	}
	
	@Override
	public void inAClassDeclCG(AClassDeclCG node) throws AnalysisException {
		
			class_list.add(node);
		
	}
	
	private LinkedList<String> superType(AClassDeclCG node,LinkedList<String> cur)
	{
		
		String sname = node.getSuperName();
		for(AClassDeclCG cls : class_list)
		{
			if(cls.getName() == sname)
			{
				cur.addAll(superType(cls, cur));
			}
		}
		
		return cur;
		
	}

	
	public LinkedList<String> getSuperType(AClassDeclCG type)
	{
		LinkedList<String> types = new LinkedList<String>();
		
		types.addAll(superType(type,types));
		
		return types;
	}

}
