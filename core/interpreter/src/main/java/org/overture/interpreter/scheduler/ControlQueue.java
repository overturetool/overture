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

package org.overture.interpreter.scheduler;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.runtime.Context;

public class ControlQueue implements Serializable
{
	private static final long serialVersionUID = 1L;
	private ISchedulableThread joined = null;
	private boolean stimmed = false;
	private List<ISchedulableThread> waiters = new LinkedList<>();

	public void reset()
	{
		joined = null;
		stimmed = false;
		waiters.clear();
	}

	public void join(Context ctxt, ILexLocation location)
	{
		ISchedulableThread th = BasicSchedulableThread.getThread(Thread.currentThread());

		if (joined != null && joined != th)
		{
			synchronized (waiters)
			{
				waiters.add(th);
			}

			th.waiting(ctxt, location);
		}

		joined = th;
	}

	public void block(Context ctxt, ILexLocation location)
	{
		if (stimmed)
		{
			stimmed = false;
		} else
		{
			joined.waiting(ctxt, location);
		}
	}

	public void stim()
	{
		stimmed = true;

		if (joined != null)
		{
			joined.setState(RunState.RUNNABLE);
		}
	}

	public void leave()
	{
		joined = null;

		ISchedulableThread head = null;

		synchronized (waiters)
		{
			if (!waiters.isEmpty())
			{
				head = waiters.remove(0);
			}
		}

		if (head != null)
		{
			head.setState(RunState.RUNNABLE);
		}
	}
}
