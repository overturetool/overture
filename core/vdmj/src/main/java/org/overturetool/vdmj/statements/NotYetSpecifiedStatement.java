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

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.IO;
import org.overturetool.vdmj.util.MATH;
import org.overturetool.vdmj.values.Value;


public class NotYetSpecifiedStatement extends Statement
{
	private static final long serialVersionUID = 1L;

	public NotYetSpecifiedStatement(LexLocation location)
	{
		super(location);
	}

	@Override
	public String toString()
	{
		return "is not yet specified";
	}

	@Override
	public String kind()
	{
		return "not specified";
	}

	@Override
	public Type typeCheck(Environment env, NameScope scope)
	{
		return new UnknownType(location);	// Because we terminate anyway
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		if (ctxt.title.equals("fecho(filename, text, fdir)"))
		{
			return IO.fecho(ctxt);
		}
		else if (ctxt.title.equals("ferror()"))
		{
			return IO.ferror();
		}
		else
		{
			try
			{
        		if (ctxt.title.equals("rand(a)"))
        		{
        		    return MATH.rand(ctxt);
        		}
        		else if (ctxt.title.equals("srand2(a)"))
        		{
        		    return MATH.srand2(ctxt);
        		}
			}
			catch (ValueException e)
			{
				throw new ContextException(e, location);
			}
			catch (Exception e)
			{
				throw new ContextException(4134, e.getMessage(), ctxt.location, ctxt);
			}
		}

		return abort(4041, "'is not yet specified' statement reached", ctxt);
	}
}
