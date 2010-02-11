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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.runtime.RootContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NaturalOneValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class NotYetSpecifiedExpression extends Expression
{
	private static final long serialVersionUID = 1L;

	public NotYetSpecifiedExpression(LexLocation location)
	{
		super(location);
		location.hit();		// ie. ignore coverage for these
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

		if (location.module.equals("VDMUtil"))
		{
    		if (ctxt.title.equals("get_file_pos()"))
    		{
    			// This needs location information from the context, so we
    			// can't just call down to a native method for this one.

    			return get_file_pos(ctxt);
    		}
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			ModuleInterpreter i = (ModuleInterpreter)Interpreter.getInstance();
			Module module = i.findModule(location.module);

			if (module != null)
			{
				if (module.hasDelegate())
				{
					return module.invokeDelegate(ctxt);
				}
			}
		}
		else
		{
    		ObjectValue self = ctxt.getSelf();

    		if (self == null)
    		{
    			ClassInterpreter i = (ClassInterpreter)Interpreter.getInstance();
    			ClassDefinition cls = i.findClass(location.module);

    			if (cls != null)
    			{
    				if (cls.hasDelegate())
    				{
    					return cls.invokeDelegate(ctxt);
    				}
    			}
    		}
    		else
    		{
    			if (self.hasDelegate())
    			{
    				return self.invokeDelegate(ctxt);
    			}
    		}
		}

		return abort(4024, "'not yet specified' expression reached", ctxt);
	}

	private Value get_file_pos(Context ctxt)
	{
		try
		{
			ValueList tuple = new ValueList();
			Context outer = ctxt.getRoot().outer;
			RootContext root = outer.getRoot();

			tuple.add(new SeqValue(ctxt.location.file.getPath()));
			tuple.add(new NaturalOneValue(ctxt.location.startLine));
			tuple.add(new NaturalOneValue(ctxt.location.startPos));
			tuple.add(new SeqValue(ctxt.location.module));

			int bra = root.title.indexOf('(');

			if (bra > 0)
			{
    			tuple.add(new SeqValue(root.title.substring(0, bra)));
			}
			else
			{
				tuple.add(new SeqValue(""));
			}

			return new TupleValue(tuple);
		}
		catch (ValueException e)
		{
			throw new ContextException(e, ctxt.location);
		}
		catch (Exception e)
		{
			throw new ContextException(4076, e.getMessage(), ctxt.location, ctxt);
		}
	}
}
