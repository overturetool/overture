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
package org.overture.tools.examplepackager;

public enum Release
{
	CLASSIC("classic"), // VDM, before Overture changes
	VDM_10("vdm10"); // VDM-10, with Overture changes

	public static final Release DEFAULT = CLASSIC;

	private String option;

	private Release(String option)
	{
		this.option = option;
	}

	@Override
	public String toString()
	{
		return option;
	}

	public static Release lookup(String release)
	{
		for (Release r : values())
		{
			if (r.option.equals(release))
			{
				return r;
			}
		}

		return null;
	}

	public static String list()
	{
		StringBuilder sb = new StringBuilder();
		String sep = "";
		sb.append("<");

		for (Release r : values())
		{
			sb.append(sep);
			sb.append(r.option);
			sep = " | ";
		}

		sb.append(">");
		return sb.toString();
	}
}
