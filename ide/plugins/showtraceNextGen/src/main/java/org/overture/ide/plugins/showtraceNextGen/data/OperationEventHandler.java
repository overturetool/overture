package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.*;

public class OperationEventHandler extends EventHandler {


	public OperationEventHandler(TraceData data) 
	{
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) {
		// TODO Auto-generated method stub
		return true;
	}



}
