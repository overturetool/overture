package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;

public class BusMessageReplyEventHandler extends EventHandler {

	public BusMessageReplyEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenBusMessageReplyRequestEvent bEvent = (NextGenBusMessageReplyRequestEvent)event;
		if(bEvent == null) return false; //Guard
		
		TraceCPU cpu = data.getCPU(new Long(bEvent.replyMessage.fromCpu.id));
		//TraceThread thread = data.getThread(bEvent.replyMessage.callerThread.id);
		TraceObject object = null;
		TraceBus bus = data.getBUS(new Long(bEvent.replyMessage.bus.id));
		TraceOperation op = data.getOperation(bEvent.replyMessage.operation.classDef.name + bEvent.replyMessage.operation.name);
				
		eventViewer.drawReplyRequest(tab,  cpu, object, bus, op);
		return true;
	}


}
