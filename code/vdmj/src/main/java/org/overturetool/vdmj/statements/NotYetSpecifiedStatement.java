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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.PrivateClassEnvironment;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.util.IO;
import org.overturetool.vdmj.util.MATH;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.BooleanValue;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.SeqValue;
import org.overturetool.vdmj.values.TupleValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;


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
		else if (ctxt.title.equals("runtrace(classname, statements)"))
		{
			return runtrace(ctxt);
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

	// runtrace : seq of char * seq of seq of char ==> seq of (seq of char * bool)
    // 		runtrace(classname, statements) == is not yet specified;

	private Value runtrace(Context ctxt)
	{
		ValueList list = new ValueList();

		try
		{
			String caller = ctxt.location.module;

			LexNameToken carg = new LexNameToken(caller, "classname", null);
			String classname = ctxt.lookup(carg).toString();
			classname = classname.substring(1, classname.length() - 1);

			LexNameToken sarg = new LexNameToken(caller, "statements", null);
			SeqValue statements = (SeqValue)ctxt.lookup(sarg);

			Interpreter ip = Interpreter.getInstance();
			ClassDefinition classdef = ip.findClass(classname);

			if (classdef == null)
			{
				throw new Exception("Class " + classname + " not found");
			}

			Environment env = new FlatEnvironment(
				classdef.getSelfDefinition(),
				new PrivateClassEnvironment(classdef, ip.getGlobalEnvironment()));

			ObjectValue object = classdef.newInstance(null, null, ip.initialContext);

			Context ectxt = new ObjectContext(
					classdef.name.location, classdef.name.name + "()",
					ip.initialContext, object);

			ctxt.put(classdef.name.getSelfName(), object);

			for (Value sval: statements.values)
			{
				String statement = sval.toString();
				statement = statement.substring(1, statement.length() - 1);
				Statement s = ip.parseStatement(statement, classname);
				ip.typeCheck(s, env);

				if (TypeChecker.getErrorCount() != 0)
				{
					List<VDMError> errors = TypeChecker.getErrors();
					throw new Exception(Utils.listToString(errors, " and "));
				}
				else
				{
					ValueList pair = new ValueList();
					pair.add(new SeqValue(s.eval(ectxt).toString()));
					pair.add(new BooleanValue(true));
					list.add(new TupleValue(pair));
				}
			}

		}
		catch (Exception e)
		{
			ValueList pair = new ValueList();
			pair.add(new SeqValue(e.getMessage()));
			pair.add(new BooleanValue(false));
			list.add(new TupleValue(pair));
		}

		return new SeqValue(list);
	}
}
