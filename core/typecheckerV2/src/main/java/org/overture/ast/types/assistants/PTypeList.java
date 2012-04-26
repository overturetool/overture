package org.overture.ast.types.assistants;

import java.util.Vector;

import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overturetool.vdmjV2.lex.LexLocation;
import org.overturetool.vdmjV2.util.Utils;

@SuppressWarnings("serial")
public class PTypeList extends Vector<PType> {

	
	public PTypeList()
	{
		super();
	}

	public PTypeList(PType act)
	{
		add(act);
	}

	@Override
	public boolean add(PType t)
	{
		return super.add(t);
	}
	
	public PType getType(LexLocation location)
	{
		PType result = null;

		if (this.size() == 1)
		{
			result = iterator().next();
		}
		else
		{
			result = new AProductType(location,false,null, this);
		}

		return result;
	}
	
	@Override
	public String toString()
	{
		return "(" + Utils.listToString(this) + ")";
	}
}
