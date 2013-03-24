package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;

public abstract class EventFilter {
	
	/**
	 * Apply filter according to the view currently being drawn.
	 * @param type The event for which the filter should be applied.
	 * @return true if the event should be drawn, false otherwise
	 */
	public boolean apply(INextGenEvent type)
	{
		boolean result = true;
		if(type != null && type instanceof NextGenOperationEvent)
		{
			//TODO: This apparently identifies a call to a static method. These are currently filtered out, however, this isn't correct.
			if(((NextGenOperationEvent)type).object == null)
			{
				result = false;
			}
		}
		
		return result;
	}
}

