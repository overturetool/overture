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

package org.overturetool.vdmj.expressions;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.IO;
import org.overturetool.vdmj.util.MATH;
import org.overturetool.vdmj.util.VDMUtil;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;

public class NotYetSpecifiedExpression extends Expression
{
	private static final long serialVersionUID = 1L;

	public NotYetSpecifiedExpression(LexLocation location)
	{
		super(location);
	}

	@Override
	public String toString()
	{
		return "not yet specified";
	}

	@Override
	public String kind()
	{
		return "not specified";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		return new UnknownType(location);	// Because we terminate anyway
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		ObjectValue self = ctxt.getSelf();

		if (self != null)
		{
			if (self.hasDelegate())
			{
				return self.invokeDelegate(ctxt);
			}
		}

		if (location.module.equals("IO"))
		{
    		if (ctxt.title.equals("writeval(val)"))
    		{
    			return IO.writeval(ctxt);
    		}
    		else if (ctxt.title.equals("fwriteval(filename, val, fdir)"))
    		{
    			return IO.fwriteval(ctxt);
    		}
    		else if (ctxt.title.equals("freadval(f)"))
    		{
    			return IO.freadval(ctxt);
    		}
		}
		else if (location.module.equals("VDMUtil"))
		{
    		if (ctxt.title.equals("set2seq(x)"))
    		{
    			return VDMUtil.set2seq(ctxt);
    		}
    		else if (ctxt.title.equals("get_file_pos()"))
    		{
    			return VDMUtil.get_file_pos(ctxt);
    		}
    		else if (ctxt.title.equals("val2seq_of_char(x)"))
    		{
    			return VDMUtil.val2seq_of_char(ctxt);
    		}
    		else if (ctxt.title.equals("seq_of_char2val(s)"))
    		{
    			return VDMUtil.seq_of_char2val(ctxt);
    		}
		}
		else if (location.module.equals("MATH"))
		{
			try
			{
        		if (ctxt.title.equals("sin(v)"))
        		{
        		    return MATH.sin(ctxt);
        		}
        		else if (ctxt.title.equals("cos(v)"))
        		{
        		    return MATH.cos(ctxt);
        		}
        		else if (ctxt.title.equals("tan(a)"))
        		{
        		    return MATH.tan(ctxt);
        		}
        		else if (ctxt.title.equals("cot(a)"))
        		{
        		    return MATH.cot(ctxt);
        		}
        		else if (ctxt.title.equals("asin(a)"))
        		{
        		    return MATH.asin(ctxt);
        		}
        		else if (ctxt.title.equals("acos(a)"))
        		{
        		    return MATH.acos(ctxt);
        		}
        		else if (ctxt.title.equals("atan(v)"))
        		{
        		    return MATH.atan(ctxt);
        		}
        		else if (ctxt.title.equals("sqrt(a)"))
        		{
        		    return MATH.sqrt(ctxt);
        		}
        		else if (ctxt.title.equals("pi_f()"))
        		{
        		    return MATH.pi(ctxt);
        		}
        		else if (ctxt.title.equals("exp(a)"))
        		{
        		    return MATH.exp(ctxt);
        		}
        		else if (ctxt.title.equals("ln(a)"))
        		{
        		    return MATH.ln(ctxt);
        		}
        		else if (ctxt.title.equals("log(a)"))
        		{
        		    return MATH.log(ctxt);
        		}
			}
			catch (ValueException e)
			{
				throw new ContextException(e, location);
			}
			catch (ContextException e)
			{
				throw e;
			}
			catch (Exception e)
			{
				throw new InternalException(34,
					"Native library error: " + e.getMessage());
			}
		}

		return abort(4024, "'not yet specified' expression reached", ctxt);
	}
}
