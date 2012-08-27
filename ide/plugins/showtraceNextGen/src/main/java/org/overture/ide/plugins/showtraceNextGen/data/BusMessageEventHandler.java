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
		
		TraceCPU cpu = data.getCPU(new Long(bEvent.message.fromCpu.id));
		TraceThread thread = data.getThread(bEvent.message.callerThread.id);
		TraceBus bus = data.getBUS(new Long(bEvent.message.bus.id));
		TraceOperation op = data.getOperation(bEvent.message.operation.classDef.name + bEvent.message.operation.name);
		
		//TODO: MVQ: Review if all objects are required in all draw methods
		switch(bEvent.type)
		{
		case REQUEST: 
			eventViewer.drawMessageRequest(tab, cpu, thread, bus, op);
			break;
		case ACTIVATE:
			eventViewer.drawMessageActivated(tab, cpu, thread, bus, op);
			break;
		case COMPLETED:
			cpu = data.getCPU(new Long(bEvent.message.toCpu.id));
			eventViewer.drawMessageCompleted(tab, cpu, thread, bus, op);
			
	        //If this is a reply to an earlier request then unblock the thread which did the request
	        if(bEvent.message.receiverThread != null)
	        {
				TraceThread thr = data.getThread(bEvent.message.receiverThread.id);
				thr.setStatus(false);
	        }        
			break;
		case REPLY_REQUEST:
			return false; //Handle by BusMesageReplyEventHandler
		default: 
			return false;
		}
		
		return true;
	}



}
