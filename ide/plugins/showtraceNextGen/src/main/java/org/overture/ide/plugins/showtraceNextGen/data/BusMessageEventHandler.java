package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;

public class BusMessageEventHandler extends EventHandler {

	public BusMessageEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenBusMessageEvent bEvent = (NextGenBusMessageEvent)event;
		if(bEvent == null) return false; //Guard
		
//		Long cpuId = new Long(tEvent.thread.cpu.id);
		//Long threadId = new Long(oEvent.thread.id);
//		TraceCPU cpu = data.getCPU(cpuId);
		//TraceThread thread = data.getThread(threadId);
				
		switch(bEvent.type)
		{			
		case ACTIVATE: 
			eventViewer.drawMessageActivated(tab, null);
			break;
		case COMPLETED: 
			eventViewer.drawMessageCompleted(tab, null);
			break;
		case REPLY_REQUEST:
			return false; //Handle by BusMesageReplyEventHandler
		case REQUEST: 
			eventViewer.drawMessageRequest(tab, null);
			break;
		default: 
			return false;
		}
		
		return true;
	}



}
