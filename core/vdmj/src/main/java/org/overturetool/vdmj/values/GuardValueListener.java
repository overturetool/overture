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

import java.io.Serializable;

import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.config.Properties;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.rtlog.RTLogger;
import org.overturetool.vdmj.messages.rtlog.RTExtendedTextMessage;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.scheduler.BasicSchedulableThread;

public class GuardValueListener implements ValueListener, Serializable
{
    private static final long serialVersionUID = 1L;
	private final ObjectValue self;

	public GuardValueListener(ObjectValue self)
	{
		this.self = self;
	}

	public void changedValue(LexLocation location, Value value, Context ctxt)
	{
		if (Properties.diags_guards)
		{
			if (Settings.dialect == Dialect.VDM_PP)
			{
				System.err.println(String.format("%s updated value %s",
						BasicSchedulableThread.getThread(Thread.currentThread()), location));
			}
			else
			{
				RTLogger.log(new RTExtendedTextMessage(String.format("-- %s updated value %s",
						BasicSchedulableThread.getThread(Thread.currentThread()), location)));
			}
		}

		self.guardLock.signal();
	}
}
