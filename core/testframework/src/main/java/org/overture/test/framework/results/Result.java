/*
 * #%~
 * Test Framework for Overture
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
package org.overture.test.framework.results;

import java.util.List;

public class Result<R>
{
	public R result;
	public final List<IMessage> warnings;
	public final List<IMessage> errors;

	public Result(R result, List<IMessage> warnings, List<IMessage> errors)
	{
		this.result = result;
		this.warnings = warnings;
		this.errors = errors;
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		if (!errors.isEmpty())
		{
			buf.append("Errors:\n");
			for (IMessage m : errors)
			{
				buf.append(m + "\n");
			}
		}

		if (!warnings.isEmpty())
		{
			buf.append("Warnings:\n");
			for (IMessage m : warnings)
			{
				buf.append(m + "\n");
			}
		}
		buf.append("Result:\n");
		buf.append(result);

		return buf.toString();
	}

	public String getStringResult()
	{
		return (result == null ? "" : "" + result).replace("\n", "");
	}
}
