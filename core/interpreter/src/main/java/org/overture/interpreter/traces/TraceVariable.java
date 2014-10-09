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

package org.overture.interpreter.traces;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.PType;
import org.overture.interpreter.values.Value;

public class TraceVariable
{
	public final ILexLocation location;
	public final ILexNameToken name;
	public final Value value;
	public final PType type;
	public final boolean clone;

	public TraceVariable(ILexLocation location, ILexNameToken name,
			Value value, PType type, boolean clone)
	{
		this.location = location;
		this.name = name;
		this.value = value;
		this.type = type;
		this.clone = clone;
	}

	@Override
	public String toString()
	{
		return name + ":" + type + " = " + value;
	}
}
