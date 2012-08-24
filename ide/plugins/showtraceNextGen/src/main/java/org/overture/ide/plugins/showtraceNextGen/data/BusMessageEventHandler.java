package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenThreadEvent;

public class BusMessageEventHandler extends EventHandler {

	public BusMessageEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) {
		// TODO Auto-generated method stub
		return true;
	}



}
