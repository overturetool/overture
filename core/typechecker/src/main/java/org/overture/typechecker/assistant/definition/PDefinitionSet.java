/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.definitions.PDefinition;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PDefinitionSet extends HashSet<PDefinition>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2315113629324204849L;

	protected ITypeCheckerAssistantFactory af;

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
		List<PDefinition> list = new ArrayList<PDefinition>();
		list.addAll(this);
		return list;
	}
}
