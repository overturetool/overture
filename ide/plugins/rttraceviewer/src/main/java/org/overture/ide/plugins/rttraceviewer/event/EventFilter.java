package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public abstract class EventFilter {
	
	/**
	 * Apply filter according to the view currently being drawn.
	 * @param type The event for which the filter should be applied.
	 * @return true if the event should be drawn, false otherwise
	 */
	public boolean apply(INextGenEvent type)
	{
		return true;
	}
}

