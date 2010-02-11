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

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.runtime.ClassInterpreter;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.runtime.ModuleInterpreter;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.Value;


public class NotYetSpecifiedStatement extends Statement
{
	private static final long serialVersionUID = 1L;

	public NotYetSpecifiedStatement(LexLocation location)
	{
		super(location);
		location.hit();		// ie. ignore coverage for these
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

		if (location.module.equals("CPU"))
		{
    		if (ctxt.title.equals("deploy(obj)"))
    		{
    			return CPUClassDefinition.deploy(ctxt);
    		}
    		else if (ctxt.title.equals("deploy(obj, name)"))
    		{
    			return CPUClassDefinition.deploy(ctxt);
    		}
    		else if (ctxt.title.equals("setPriority(opname, priority)"))
    		{
    			return CPUClassDefinition.setPriority(ctxt);
    		}
		}

		return abort(4041, "'is not yet specified' statement reached", ctxt);
	}
}
