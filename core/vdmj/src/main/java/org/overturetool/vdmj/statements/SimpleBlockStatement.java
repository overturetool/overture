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

package org.overturetool.vdmj.statements;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.types.VoidReturnType;
import org.overturetool.vdmj.types.VoidType;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.VoidValue;


abstract public class SimpleBlockStatement extends Statement
{
	private static final long serialVersionUID = 1L;
	public final List<Statement> statements = new Vector<Statement>();

	public SimpleBlockStatement(LexLocation location)
	{
		super(location);
		location.executable(false);
	}

	public void add(Statement stmt)
	{
		statements.add(stmt);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";

		for (Statement s: statements)
		{
			sb.append(sep);
			sb.append(s.toString());
			sep = ";\n";
		}

		sb.append("\n");
		return sb.toString();
	}

	@Override
	public String kind()
	{
		return "block";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		boolean notreached = false;
		TypeSet rtypes = new TypeSet();

		for(Statement stmt: statements)
		{
			Type st = stmt.typeCheck(env, scope);

			if (notreached)
			{
				stmt.warning(5006, "Statement will not be reached");
			}
			else
			{
				notreached = true;

    			if (st instanceof UnionType)
    			{
    				UnionType ust = (UnionType)st;

    				for (Type t: ust.types)
    				{
    					addOne(rtypes, t);

    					if (t instanceof VoidType ||
    						t instanceof UnknownType)
    					{
    						notreached = false;
    					}
    				}
    			}
    			else
    			{
    				addOne(rtypes, st);

					if (st instanceof VoidType ||
						st instanceof UnknownType)
					{
						notreached = false;
					}
    			}
			}
		}

		return rtypes.isEmpty() ?
			new VoidType(location) : rtypes.getType(location);
	}

	private void addOne(TypeSet rtypes, Type add)
	{
		if (add instanceof VoidReturnType)
		{
			rtypes.add(new VoidType(add.location));
		}
		else if (!(add instanceof VoidType))
		{
			rtypes.add(add);
		}
	}

	@Override
	public TypeSet exitCheck()
	{
		TypeSet types = new TypeSet();

		for (Statement stmt: statements)
		{
			types.addAll(stmt.exitCheck());
		}

		return types;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		if (location.startLine == lineno) return this;
		Statement found = null;

		for (Statement stmt: statements)
		{
			found = stmt.findStatement(lineno);
			if (found != null) break;
		}

		return found;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = null;

		for (Statement stmt: statements)
		{
			found = stmt.findExpression(lineno);
			if (found != null) break;
		}

		return found;
	}

	@Override
	abstract public Value eval(Context ctxt);

	protected Value evalBlock(Context ctxt)
	{
		// Note, no breakpoint check - designed to be called by eval

		if (Settings.dialect == Dialect.VDM_RT &&
			ctxt.threadState.getTimestep() < 0)
		{
			int time = Properties.rt_duration_default;

			for (Statement s: statements)
			{
				Value rv = s.eval(ctxt);
				ctxt.threadState.CPU.duration(time);

				if (!rv.isVoid())
    			{
    				return rv;
    			}
			}
		}
		else
		{
    		for (Statement s: statements)
    		{
    			Value rv = s.eval(ctxt);

    			if (!rv.isVoid())
    			{
    				return rv;
    			}
    		}
		}

		return new VoidValue();
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (Statement stmt: statements)
		{
			obligations.addAll(stmt.getProofObligations(ctxt));
		}

		return obligations;
	}
}
