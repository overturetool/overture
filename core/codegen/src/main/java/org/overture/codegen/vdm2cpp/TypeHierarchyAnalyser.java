package org.overture.codegen.vdm2cpp;

import java.util.ArrayList;
import java.util.LinkedList;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

/**
 * 
 * Class for getting gathering the VDM++ class hierarchy 
 *
 */
public class TypeHierarchyAnalyser extends DepthFirstAnalysisAdaptor {
	
	private ArrayList<AClassDeclCG> class_list;
	
	public TypeHierarchyAnalyser() {
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
			if(cls.getName().equals(sname))
			{
				cur.add(cls);
				superType(cls, cur);
			}
		}
		return cur;
	}

	
	public LinkedList<AClassDeclCG> getSuperType(AClassDeclCG type)
	{
		LinkedList<AClassDeclCG> types = new LinkedList<AClassDeclCG>();
		
		superType(type,types);
		
		return types;
	}

}
