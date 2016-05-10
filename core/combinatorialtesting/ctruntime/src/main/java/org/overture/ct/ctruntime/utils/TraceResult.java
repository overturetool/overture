/*
 * #%~
 * Combinatorial Testing Runtime
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ct.ctruntime.utils;

import java.util.List;
import java.util.Vector;

public class TraceResult
{
	public String traceName;
	public List<TraceTest> tests = new Vector<>();

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Trace name: " + traceName).append("\n");

		for (TraceTest test : tests)
		{
			sb.append(test).append("\n");
		}

		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		int hashCode = 0;

		hashCode += traceName != null ? traceName.hashCode() : 0;

		for (TraceTest test : tests)
		{
			hashCode += test.hashCode();
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (getClass() != obj.getClass())
		{
			return false;
		}

		final TraceResult other = (TraceResult) obj;

		if (this.traceName == null ? other.traceName != null
				: !this.traceName.equals(other.traceName))
		{
			return false;
		}

		return tests == null ? other.tests == null : tests.equals(other.tests);
	}
}
