package org.overture.interpreter.types.assistant;

import java.util.Vector;


import org.overture.interpreter.ast.types.AProductTypeInterpreter;
import org.overture.interpreter.ast.types.PTypeInterpreter;
import org.overturetool.interpreter.vdmj.lex.LexLocation;
import org.overturetool.vdmj.util.Utils;

@SuppressWarnings("serial")
public class PTypeInterpreterList extends Vector<PTypeInterpreter> {

	
	public PTypeInterpreterList()
	{
		super();
	}

	public PTypeInterpreterList(PTypeInterpreter act)
	{
		add(act);
	}

	@Override
	public boolean add(PTypeInterpreter t)
	{
		return super.add(t);
	}
	
	public PTypeInterpreter getType(LexLocation location)
	{
		PTypeInterpreter result = null;

		if (this.size() == 1)
		{
			result = iterator().next();
		}
		else
		{
			result = new AProductTypeInterpreter(location,false, this);
		}

		return result;
	}
	
	@Override
	public String toString()
	{
		return "(" + Utils.listToString(this) + ")";
	}
}
