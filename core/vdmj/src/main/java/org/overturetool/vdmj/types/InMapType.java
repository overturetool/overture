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

package org.overturetool.vdmj.types;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.MapValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class InMapType extends MapType
{
	private static final long serialVersionUID = 1L;

	public InMapType(LexLocation location, Type from, Type to)
	{
		super(location, from, to);
	}

	@Override
	public String toDisplay()
	{
		return "inmap (" + from + ") to (" + to + ")";
	}
	
	@Override
	public ValueList getAllValues(Context ctxt) throws ValueException
	{
		ValueList maps = super.getAllValues(ctxt);
		ValueList result = new ValueList();
		
		for (Value map: maps)
		{
			MapValue vm = (MapValue)map;
			
			if (vm.values.isInjective())
			{
				result.add(vm);
			}
		}
		
		return result;
	}
}
