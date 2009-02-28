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

package org.overturetool.vdmj.types;

import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.Value;

public abstract class InvariantType extends Type
{
	public ExplicitFunctionDefinition invdef = null;

	public InvariantType(LexLocation location)
	{
		super(location);
	}

	@Override
	abstract protected String toDisplay();

	public void setInvariant(ExplicitFunctionDefinition invdef)
	{
		this.invdef = invdef;
	}

	public FunctionValue getInvariant(Context ctxt)
	{
		if (invdef != null)
		{
			try
			{
				Value v = ctxt.getGlobal().lookup(invdef.name);
				return v.functionValue(ctxt);
			}
			catch (ValueException e)
			{
				abort(e);
			}
		}

		return null;
	}
}
