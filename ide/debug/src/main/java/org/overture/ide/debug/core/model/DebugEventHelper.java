/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.model;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IDebugElement;

public final class DebugEventHelper
{
	private DebugEventHelper()
	{
	}

	private static void fireEvent(DebugEvent event)
	{
		if (DebugPlugin.getDefault() != null)
		{
			DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { event });
		}
	}

	public static void fireCreateEvent(IDebugElement element)
	{
		fireEvent(new DebugEvent(element, DebugEvent.CREATE));
	}

	public static void fireResumeEvent(IDebugElement element, int detail)
	{
		fireEvent(new DebugEvent(element, DebugEvent.RESUME, detail));

	}

	public static void fireSuspendEvent(IDebugElement element, int detail)
	{
		fireEvent(new DebugEvent(element, DebugEvent.SUSPEND, detail));
	}

	public static void fireTerminateEvent(IDebugElement element)
	{
		fireEvent(new DebugEvent(element, DebugEvent.TERMINATE));
	}

	public static void fireChangeEvent(IDebugElement element)
	{
		fireEvent(new DebugEvent(element, DebugEvent.CHANGE));
	}

	public static void fireExtendedEvent(Object eventSource, int details)
	{
		fireEvent(new DebugEvent(eventSource, DebugEvent.MODEL_SPECIFIC, details));
	}
}
