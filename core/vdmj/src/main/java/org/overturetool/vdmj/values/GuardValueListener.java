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

package org.overturetool.vdmj.values;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.CPUThread;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.RunState;

public class GuardValueListener<T> implements ValueListener
{
	private final T object;

	public GuardValueListener(T object)
	{
		this.object = object;
	}

	public void changedValue(LexLocation location, Value value, Context ctxt)
	{
		if (Settings.dialect == Dialect.VDM_RT)
		{
			ObjectValue self = (ObjectValue)object;

			for (CPUThread th: self.guardWaiters)
			{
				th.setState(RunState.RUNNABLE);
			}
		}
		else
		{
    		synchronized (object)
    		{
    			object.notifyAll();
    		}
		}
	}
}
