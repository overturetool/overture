/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.modules;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.util.Utils;


@SuppressWarnings("serial")
public class ModuleList extends Vector<Module>
{
	public ModuleList()
	{
		// empty
	}

	public ModuleList(List<Module> modules)
	{
		addAll(modules);
	}

	@Override
	public String toString()
	{
		return Utils.listToString(this);
	}

	public Module findModule(LexIdentifierToken sought)
	{
		for (Module m: this)
		{
			if (m.name.equals(sought))
			{
				return m;
			}
		}

   		return null;
	}

	public StateContext initialize()
	{
		StateContext initialContext = null;

		if (isEmpty())
		{
			initialContext = new StateContext(
				new LexLocation("file", null, 0, 0, 0, 0), "global environment");
		}
		else
		{
			initialContext =
				new StateContext(this.get(0).name.location, "global environment");
		}

		ContextException problems = null;
		int retries = 2;

		do
		{
			problems = null;

        	for (Module m: this)
    		{
        		ContextException e = m.initialize(initialContext);

        		if (e != null)
        		{
        			problems = e;
        		}
     		}
		}
		while (--retries > 0 && problems != null);

		if (problems != null)
		{
			throw problems;		// ... out of pram :)
		}

		return initialContext;
	}

	public ProofObligationList getProofObligations()
	{
		ProofObligationList obligations = new ProofObligationList();

		for (Module m: this)
		{
			obligations.addAll(m.getProofObligations());
		}

		return obligations;
	}

	public void setLoaded()
	{
		for (Module m: this)
		{
			m.loaded = true;
		}
	}
}
