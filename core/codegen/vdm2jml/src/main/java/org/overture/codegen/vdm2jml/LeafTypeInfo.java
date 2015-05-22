package org.overture.codegen.vdm2jml;

import org.overture.ast.types.PType;

public class LeafTypeInfo extends AbstractTypeInfo
{
	private PType type;
	
	public LeafTypeInfo(PType type, boolean optional)
	{
		super(optional);
		this.type = type;
	}
	
	public PType getType()
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
}
