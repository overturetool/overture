package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.ide.plugins.rttraceviewer.data.TraceBus;
import org.overture.ide.plugins.rttraceviewer.data.TraceBusMessage;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceObject;
import org.overture.ide.plugins.rttraceviewer.data.TraceOperation;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageReplyRequestEvent;

public class BusMessageReplyEventHandler extends EventHandler {

	public BusMessageReplyEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) {
				
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
		return;
	}


}
