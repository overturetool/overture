package org.overture.ide.plugins.showtraceNextGen.event;

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public class OverviewEventFilter implements EventFilter {
	
	@Override
	public boolean apply(INextGenEvent type) {
		return true;
	}

}
