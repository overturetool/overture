package org.overture.ide.debug.core.model;

import java.util.List;
import java.util.Vector;

public class VdmGroupValue extends VdmMultiValue
{

	public VdmGroupValue(String referenceTypeName, String type, String key,
			Integer page, Integer pageSize,Integer numChildren ,VdmVariable[] variables) {
		super(referenceTypeName, type, key, page, variables);
		
		List<VdmVariable> vars = new Vector<VdmVariable>();
		
		vars.add(new VdmVariable(null, "[1..."+(pageSize+1)+"]", referenceTypeName, new VdmMultiValue(referenceTypeName, type, null, page, variables),true));
		page++;
		while(page * pageSize < numChildren)
		{
			Integer begin = page*pageSize;
			Integer end = (page+1)*pageSize;
			
			String name = "["+(begin+1)+"..."+(end+1)+"]";
			
			vars.add(new VdmVariable(null,name,referenceTypeName,new VdmMultiValue( referenceTypeName,type,key,page,null ),true));
			page++;
		}
		super.variables = vars.toArray(new VdmVariable[vars.size()]);
		super.isResolved = true;
	}
	
	
}
