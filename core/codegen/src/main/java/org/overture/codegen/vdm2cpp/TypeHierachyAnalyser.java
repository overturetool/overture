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
	
	private LinkedList<AClassDeclCG> superType(AClassDeclCG node,LinkedList<AClassDeclCG> cur)
	{
		
		String sname = node.getSuperName();
		for(AClassDeclCG cls : class_list)
		{
			//System.out.println("comparing" + cls.getName() + " with: " + sname);
			if(cls.getName().equals(sname))
			{
				//System.out.println("found super" + cls);
				cur.add(cls);
				superType(cls, cur);
			}
		}
		//System.out.println("returning" + cur);
		return cur;
		
	}

	
	public LinkedList<AClassDeclCG> getSuperType(AClassDeclCG type)
	{
		//System.out.print(class_list);
		LinkedList<AClassDeclCG> types = new LinkedList<AClassDeclCG>();
		
		superType(type,types);
		
		return types;
	}

}
