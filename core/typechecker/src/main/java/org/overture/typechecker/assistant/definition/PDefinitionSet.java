package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PDefinitionSet extends HashSet<PDefinition>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315113629324204849L;

	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionSet(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public boolean add(PDefinition e)
	{
		if (!contains(e))
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
			if (af.createPDefinitionAssistant().equals(def, o))
			{
				return true;
			}
		}

		return false;
	}

	public List<PDefinition> asList()
	{
		List<PDefinition> list = new Vector<PDefinition>();
		list.addAll(this);
		return list;
	}
}
