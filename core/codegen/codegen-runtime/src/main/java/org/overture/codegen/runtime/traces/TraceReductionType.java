/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009, 2010 Fujitsu Services Ltd.
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

package org.overture.codegen.runtime.traces;

public enum TraceReductionType
{
	NONE("None"), RANDOM("Random"), SHAPES_NOVARS(
			"No variables"), SHAPES_VARNAMES(
					"Variable names"), SHAPES_VARVALUES("Variable values");

	private String displayName;

	private TraceReductionType(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public static TraceReductionType findValue(String displayName)
	{
		for (TraceReductionType t : values())
		{
			if (t.displayName.equals(displayName))
			{
				return t;
			}
		}

		return null;
	}
}
