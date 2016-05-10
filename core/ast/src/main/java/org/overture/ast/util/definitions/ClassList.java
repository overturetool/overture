/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util.definitions;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.SClassDefinition;

/**
 * A class for holding a list of ClassDefinitions.
 */

public class ClassList extends Vector<SClassDefinition>
{
	private static final long serialVersionUID = 1L;

	protected static Map<String, SClassDefinition> map = new HashMap<String, SClassDefinition>();

	public ClassList()
	{
		super();
	}

	public ClassList(SClassDefinition definition)
	{
		add(definition);
	}

	@Override
	public boolean add(SClassDefinition cdef)
	{
		map.put(cdef.getName().getName(), cdef);

		return super.add(cdef);
	}

	@Override
	public boolean addAll(Collection<? extends SClassDefinition> clist)
	{
		for (SClassDefinition cls : clist)
		{
			add(cls);
		}

		return true;
	}

	public void remap()
	{
		map.clear();

		for (SClassDefinition d : this)
		{
			map.put(d.getName().getName(), d);
		}
	}

	public Set<File> getSourceFiles()
	{
		Set<File> files = new HashSet<>();

		for (SClassDefinition def : this)
		{
			if (!(def instanceof ACpuClassDefinition || def instanceof ABusClassDefinition))
			{
				files.add(def.getLocation().getFile());
			}
		}

		return files;
	}

	// public void implicitDefinitions(Environment env)
	// {
	// for (SClassDefinition d: this)
	// {
	// d.implicitDefinitions(env);
	// }
	// }

	// public void setLoaded()
	// {
	// for (SClassDefinition d: this)
	// {
	// d.typechecked = true;
	// }
	// }

	// public int notLoaded()
	// {
	// int count = 0;
	//
	// for (SClassDefinition d: this)
	// {
	// if (!d.typechecked) count++;
	// }
	//
	// return count;
	// }

	// public void unusedCheck()
	// {
	// for (SClassDefinition d: this)
	// {
	// d.unusedCheck();
	// }
	// }

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (SClassDefinition c : this)
		{
			sb.append(c.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	// public ProofObligationList getProofObligations()
	// {
	// ProofObligationList obligations = new ProofObligationList();
	//
	// for (SClassDefinition c: this)
	// {
	// obligations.addAll(c.getProofObligations(new POContextStack()));
	// }
	//
	// obligations.trivialCheck();
	// return obligations;
	// }
}
