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
		
		NextGenOperationEvent oEvent = (NextGenOperationEvent)event;
		if(oEvent == null) return false; //Guard
		
		Long cpuId = new Long(oEvent.thread.cpu.id);
		Long threadId = new Long(oEvent.thread.id);
		TraceCPU cpu = data.getCPU(cpuId);
		TraceThread thread = data.getThread(threadId);
				
		switch(oEvent.type)
		{
			
		case REQUEST: 
			eventViewer.drawOpRequest(tab, cpu, thread);
			break;
		case ACTIVATE: 
			eventViewer.drawOpActivate(tab,  cpu, thread);
			break;
		case COMPLETE: 
			eventViewer.drawOpCompleted(tab,  cpu, thread);
			break;
		default: return false;
		}
		
		return true;
	}



}
