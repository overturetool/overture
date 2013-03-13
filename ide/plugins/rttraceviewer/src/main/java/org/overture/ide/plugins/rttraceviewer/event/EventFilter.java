package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;

public interface EventFilter {
	boolean apply(INextGenEvent type);
}

