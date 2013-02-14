/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others. Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version. Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of the GNU General Public
 * License along with Overture. If not, see <http://www.gnu.org/licenses/>. The Overture Tool web-site:
 * http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.framework.results;

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
		if (errors.size() > 0)
		{
			buf.append("Errors:\n");
			for (IMessage m : errors)
			{
				buf.append(m + "\n");
			}
		}

		if (warnings.size() > 0)
		{
			buf.append("Warnings:\n");
			for (IMessage m : warnings)
			{
				buf.append(m + "\n");
			}
		}
		buf.append("Result:\n");
		return buf.toString();
	}
	
	public String getStringResult()
	{
		return (result==null?"":""+result).replace("\n", "");
	}
}
