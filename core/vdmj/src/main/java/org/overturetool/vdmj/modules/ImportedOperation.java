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

package org.overturetool.vdmj.modules;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.Type;

public class ImportedOperation extends ImportedValue
{
	private static final long serialVersionUID = 1L;

	public ImportedOperation(LexNameToken name, Type type,	LexNameToken renamed)
	{
		super(name, type, renamed);
	}

	@Override
	public String toString()
	{
		return "import operation " + name +
				(renamed == null ? "" : " renamed " + renamed.name) +
				(type == null ? "" : ":" + type);
	}
}
