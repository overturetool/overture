/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
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

package org.overturetool.vdmj.runtime;

import java.util.HashSet;
import java.util.Set;

/**
 * A class containing all active VDM threads.
 */

public class VDMThreadSet
{
	private static Set<VDMThread> threads = new HashSet<VDMThread>();
	private static int debugStopped = 0;

	public static synchronized void init()
	{
		threads.clear();
		debugStopped = 0;
	}

	public static synchronized void add(VDMThread th)
	{
		threads.add(th);
	}

	public static synchronized void remove(VDMThread th)
	{
		threads.remove(th);
	}

	public static synchronized void blockAll()
	{
		for (VDMThread th: threads)
		{
			th.block();
		}
	}

	public static synchronized void unblockAll()
	{
		for (VDMThread th: threads)
		{
			th.unblock();
		}
	}

	public static synchronized void abortAll()
	{
		for (VDMThread th: threads)
		{
			th.abort();
		}
	}

	public static synchronized boolean abortAll(int secs)
	{
		abortAll();

		for (int loop=0; loop < secs; loop++)
		{
			if (threads.isEmpty())
			{
				return true;
			}

			try { Thread.sleep(1000); } catch (Exception e) { /**/ }
		}

		return threads.isEmpty();
	}

	public static synchronized String getStatus()
	{
		StringBuilder sb = new StringBuilder();

		for (VDMThread th: threads)
		{
			sb.append(th.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	public static synchronized String dump()
	{
		StringBuilder sb = new StringBuilder();

		for (VDMThread th: threads)
		{
			sb.append(th.title);
			sb.append("\n");
		}

		return sb.toString();
	}

	public static synchronized boolean isDebugStopped()
	{
		return debugStopped > 0;
	}

	public static synchronized void incDebugStopped()
	{
		debugStopped++;
	}

	public static synchronized void decDebugStopped()
	{
		debugStopped--;
	}
}
