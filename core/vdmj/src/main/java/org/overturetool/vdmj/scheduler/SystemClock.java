/*******************************************************************************
 *
 *	Copyright (c) 2010 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.scheduler;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.RTLogger;
import org.overturetool.vdmj.values.TransactionValue;

public class SystemClock
{
	private static long wallTime = 0;

	public static synchronized long getWallTime()
	{
		return wallTime;
	}

	public static void init()
	{
		wallTime = 0;
	}

	public static synchronized void advance(long duration)
	{
		wallTime += duration;

		if (Settings.dialect == Dialect.VDM_RT)
		{
			if (Properties.diags_timestep)
			{
				RTLogger.log(String.format("-- Moved time by %d", duration));
			}

			TransactionValue.commitAll();
		}
	}
}
