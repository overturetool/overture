package org.overture.ide.plugins.showtraceNextGen.data;

import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;

public class BusMessageReplyEventHandler extends EventHandler {

	public BusMessageReplyEventHandler(TraceData data, ConjectureData conjectures) {
		super(data, conjectures);
	}

	@Override
	protected boolean handle(INextGenEvent event, GenericTabItem tab) {
				
		NextGenBusMessageReplyRequestEvent bEvent = null;
		
		if(event instanceof NextGenBusMessageReplyRequestEvent)
			bEvent = (NextGenBusMessageReplyRequestEvent)event;
		else
			throw new IllegalArgumentException("BusMessageReplyEventHandler expected event of type: " + NextGenBusMessageReplyRequestEvent.class.getName());
		

		TraceBusMessage msg = data.getMessage(bEvent.replyMessage.id);
		TraceCPU fromCpu = data.getCPU(msg.getFromCpu());
		TraceThread fromThread = data.getThread(msg.getFromThread());
		TraceObject object = fromThread.getCurrentObject();	
		TraceBus bus = data.getBUS(msg.getBusId());
		TraceOperation op = data.getOperation(bEvent.replyMessage.operation.classDef.name + bEvent.replyMessage.operation.name);
				
		eventViewer.drawReplyRequest(tab,  fromCpu, object, bus, op);
		return true;
	}


}
