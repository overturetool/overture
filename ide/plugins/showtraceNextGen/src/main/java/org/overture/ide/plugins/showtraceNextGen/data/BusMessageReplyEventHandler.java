package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenOperationEvent;

public class BusMessageReplyEventHandler extends EventHandler {

	public BusMessageReplyEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenBusMessageReplyRequestEvent bEvent = (NextGenBusMessageReplyRequestEvent)event;
		if(bEvent == null) return false; //Guard
		
//		Long cpuId = new Long(tEvent.thread.cpu.id);
//		Long threadId = new Long(oEvent.thread.id);
////		TraceCPU cpu = data.getCPU(cpuId);
//		TraceThread thread = data.getThread(threadId);
				
		eventViewer.drawReplyRequest(tab,  null);
		return true;
	}


}
