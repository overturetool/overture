/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.syntax;

import java.util.HashMap;

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ContextException;
import org.overturetool.vdmj.runtime.VDMThreadSet;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.ValueList;

public class SystemDefinition extends ClassDefinition
{
	private static final long serialVersionUID = 1L;

	public SystemDefinition(LexNameToken className, DefinitionList members)
	{
		super(className, new LexNameList(), members);
	}

	@Override
	public void implicitDefinitions(Environment publicClasses)
	{
		super.implicitDefinitions(publicClasses);

		for (Definition d: definitions)
		{
			if (d instanceof InstanceVariableDefinition)
			{
				// Fine
			}
			else if (d instanceof ExplicitOperationDefinition)
			{
				ExplicitOperationDefinition edef = (ExplicitOperationDefinition)d;

				if (!edef.name.name.equals(name.name) ||
					!edef.parameterPatterns.isEmpty())
				{
					d.report(3285, "System class can only define a default constructor");
				}
			}
			else if (d instanceof ImplicitOperationDefinition)
			{
				ImplicitOperationDefinition idef = (ImplicitOperationDefinition)d;

				if (!d.name.name.equals(name.name))
				{
					d.report(3285, "System class can only define a default constructor");
				}

				if (idef.body == null)
				{
					d.report(3283, "System class constructor cannot be implicit");
				}
			}
			else
			{
				d.report(3284, "System class can only define instance variables and a constructor");
			}
		}
	}

	public void init(Context ctxt)
	{
		try
		{
   			makeNewInstance(null, new ValueList(),
    				ctxt, new HashMap<LexNameToken, ObjectValue>());

			// Show the main thread creation...

			Console.out.println(
				"ThreadCreate -> id: " + Thread.currentThread().getId() +
				" period: false objref: nil" +
				" clnm: nil" +
				" cpunm: 0" +
				" time: " + VDMThreadSet.getWallTime());

		}
		catch (ValueException e)
		{
			throw new ContextException(e, location);
		}
	}

	@Override
	public ObjectValue newInstance(
		Definition ctorDefinition, ValueList argvals, Context ctxt)
		throws ValueException
	{
		abort(4135, "Cannot instantiate system class " + name, ctxt);
		return null;
	}
}
