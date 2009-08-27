/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.definitions;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.debug.DBGPReader;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.SystemClock;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.syntax.SystemDefinition;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.values.TransactionValue;


/**
 * A class for holding a list of ClassDefinitions.
 */

public class ClassList extends Vector<ClassDefinition>
{
	private static final long serialVersionUID = 1L;

	private static Map<String, ClassDefinition> map =
					new HashMap<String, ClassDefinition>();

	public ClassList()
	{
		super();
	}

	public ClassList(ClassDefinition definition)
	{
		add(definition);
	}

	@Override
	public boolean add(ClassDefinition cdef)
	{
		map.put(cdef.name.name, cdef);

		return super.add(cdef);
	}

	@Override
	public boolean addAll(Collection<? extends ClassDefinition> clist)
	{
		for (ClassDefinition cls: clist)
		{
			add(cls);
		}

		return true;
	}

	public void remap()
	{
		map.clear();

		for (ClassDefinition d: this)
		{
			map.put(d.name.name, d);
		}
	}

	public Set<File> getSourceFiles()
	{
		Set<File> files = new HashSet<File>();

		for (ClassDefinition def: this)
		{
			files.add(def.location.file);
		}

		return files;
	}

	public void implicitDefinitions(Environment env)
	{
		for (ClassDefinition d: this)
		{
			d.implicitDefinitions(env);
		}
	}

	public void setLoaded()
	{
		for (ClassDefinition d: this)
		{
			d.typechecked = true;
		}
	}

	public int notLoaded()
	{
		int count = 0;

		for (ClassDefinition d: this)
		{
			if (!d.typechecked) count++;
		}

		return count;
	}

	public void unusedCheck()
	{
		for (ClassDefinition d: this)
		{
			d.unusedCheck();
		}
	}

	public RootContext initialize(DBGPReader dbgp)
	{
		SystemDefinition systemClass = null;
		StateContext globalContext = null;

		if (isEmpty())
		{
			globalContext = new StateContext(
				new LexLocation(), "global environment");
		}
		else
		{
			globalContext =	new StateContext(
				this.get(0).location, "public static environment");
		}

		if (Settings.dialect == Dialect.VDM_RT)
		{
			for (ClassDefinition cdef: this)
			{
    			if (cdef instanceof SystemDefinition)
    			{
    				systemClass = (SystemDefinition)cdef;
    			}
			}

			SystemClock.init();				// Set time back to zero
			CPUClassDefinition.init();
			BUSClassDefinition.init();
			CPUClassDefinition.virtualCPU.swapinMainThread();

			if (systemClass != null)
			{
				systemClass.CPUdecls();
			}
		}

		globalContext.setThreadState(dbgp, CPUClassDefinition.virtualCPU);

		// Initialize all the functions/operations first because the values
		// "statics" can call them.

		for (ClassDefinition cdef: this)
		{
			cdef.staticInit(globalContext);
		}

		// Values can forward reference each other, which means that we don't
		// know what order to initialize the classes in. So we have a crude
		// retry mechanism, looking for "forward reference" like exceptions.

		ContextException failed = null;
		int retries = 3;	// Potentially not enough.

		do
		{
			failed = null;

    		for (ClassDefinition cdef: this)
    		{
    			try
    			{
    				cdef.staticValuesInit(globalContext);
    			}
    			catch (ContextException e)
    			{
    				// These two exceptions mean that a member could not be
    				// found, which may be a forward reference, so we retry...

    				if (e.number == 4034 || e.number == 6)
    				{
    					failed = e;
    				}
    				else
    				{
    					throw e;
    				}
    			}
    		}
		}
		while (--retries > 0 && failed != null);

		if (failed != null)
		{
			throw failed;
		}

		// If we're VDM-RT and we have a system class, we need to "run"
		// the default constructor to deploy the objects declared. We
		// also have to commit any transactional updates made.

		if (systemClass != null)
		{
			systemClass.init(globalContext);
			TransactionValue.commitAll();
		}

		return globalContext;
	}

	public Definition findName(LexNameToken name, NameScope scope)
	{
		ClassDefinition d = map.get(name.module);

		if (d != null)
		{
			Definition def = d.findName(name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public Definition findType(LexNameToken name)
	{
		for (ClassDefinition d: this)
		{
			Definition def = d.findType(name);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public DefinitionSet findMatches(LexNameToken name)
	{
		DefinitionSet set = new DefinitionSet();

		for (ClassDefinition d: this)
		{
			set.addAll(d.findMatches(name));
		}

		return set;
	}

	public Statement findStatement(File file, int lineno)
	{
		for (ClassDefinition c: this)
		{
			if (c.name.location.file.equals(file))
			{
    			Statement stmt = c.findStatement(lineno);

    			if (stmt != null)
    			{
    				return stmt;
    			}
			}
		}

		return null;
	}

	public Expression findExpression(File file, int lineno)
	{
		for (ClassDefinition c: this)
		{
			if (c.name.location.file.equals(file))
			{
    			Expression exp = c.findExpression(lineno);

    			if (exp != null)
    			{
    				return exp;
    			}
			}
		}

		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		for (ClassDefinition c: this)
		{
			sb.append(c.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	public ProofObligationList getProofObligations()
	{
		ProofObligationList obligations = new ProofObligationList();

		for (ClassDefinition c: this)
		{
			obligations.addAll(c.getProofObligations(new POContextStack()));
		}

		return obligations;
	}
}
