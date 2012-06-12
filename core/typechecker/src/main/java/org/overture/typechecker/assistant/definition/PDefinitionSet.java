package org.overture.typechecker.assistant.definition;

import java.util.HashSet;

import org.overture.ast.definitions.PDefinition;

public class PDefinitionSet extends HashSet<PDefinition>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315113629324204849L;


	@Override
	public boolean add(PDefinition e)
	{
		if(!contains(e))
		{
			return super.add(e);
		}
		
		return false;
	}
	
	
	@Override
	public boolean contains(Object o)
	{
		for (PDefinition def : this)
		{
			if(PDefinitionAssistantTC.equals(def, o))
			{
				return true;
			}
		}
		
		return false;
	}
	
}
