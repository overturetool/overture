package org.overture.codegen.vdm2cpp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.NodeList;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;


public class TypeDependency extends DepthFirstAnalysisAdaptor {

	private ArrayList<TypeContainer> temporary_list;
	private ArrayList<TypeContainer> final_list;
	private ArrayList<INode> node_list;
	
	public TypeDependency() {
		// TODO Auto-generated constructor stub
		temporary_list = new ArrayList<TypeContainer>();
		final_list = new ArrayList<TypeContainer>();
		node_list = new ArrayList<INode>();
	}
	
	@Override
	public void inARecordDeclCG(ARecordDeclCG node) throws AnalysisException {
		// TODO Auto-generated method stub
		
		AClassDeclCG name = node.getAncestor(AClassDeclCG.class);
		
		TypeContainer t = new TypeContainer(node.getName(), name.getName());
		
		if(temporary_list.contains(t))
		{
			// abort
			throw new AnalysisException("Cyclic dependency found");
		}
		
		temporary_list.add(t);
		
		if(!final_list.contains(t))
		{
			Map<String, Object> m = node.getChildren(false);
			
			for(String n : m.keySet())
			{
				Object ob = m.get(n);
				if(ob instanceof NodeList<?>)
				{
					NodeList<INode> nl = ( (NodeList<INode>) m.get(n) );
					for(INode nn : nl)
					{
						nn.apply(this);
					}
				}
				//System.out.println();
			}
			
			final_list.add(t);
			node_list.add(node);
		}
	}
	
	public ArrayList<TypeContainer> getOrderedDependencies()
	{
		Collections.reverse(final_list);
		return final_list;
	}
	
	public ArrayList<INode> getOrderedRecords()
	{
		Collections.reverse(node_list);
		return node_list;
	}
}
