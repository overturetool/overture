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

import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnknownType;

public class ExternalClause
{
	public final LexToken mode;
	public final LexNameList identifiers;
	public Type type;

	public ExternalClause(LexToken mode, LexNameList names, Type type)
	{
		this.mode = mode;
		this.identifiers = names;
		this.type = (type == null) ? new UnknownType(names.get(0).location) : type;
	}

	public void typeResolve(Environment base)
	{
		type = type.typeResolve(base, null);
	}

	@Override
	public String toString()
	{
		return mode.toString() + " " + identifiers + (type == null ? "" : ":" + type);
	}
}
