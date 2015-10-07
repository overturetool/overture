package org.overture.codegen.vdm2jml;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.STypeCG;

public class LeafTypeInfo extends AbstractTypeInfo
{
	private STypeCG type;
	
	public LeafTypeInfo(STypeCG type, boolean optional)
	{
		super(optional);
		this.type = type;
	}
	
	public STypeCG getType()
	{
		return type;
	}

	@Override
	public boolean allowsNull()
	{
		return optional;
	}
	
	@Override
	public String toString()
	{
		if(optional)
		{
			return "[" + type.toString() + "]";
		}
		else
		{
			return type.toString();
		}
	}


	@Override
	public List<LeafTypeInfo> getLeafTypesRecursively()
	{
		List<LeafTypeInfo> types = new LinkedList<LeafTypeInfo>();
		types.add(this);
		
		return types;
	}
}
