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
		
		if(bEvent.message.callerThread.object == null) return true; //TODO: MAA: There is no caller thread.object for MAIN and INIT Thread and utils! Causes exception
		
		TraceCPU fromCpu = data.getCPU(new Long(bEvent.message.fromCpu.id));
		TraceCPU toCpu = data.getCPU(new Long(bEvent.message.toCpu.id));
		TraceThread callerthread = data.getThread(bEvent.message.callerThread.id);
		TraceThread receiverThread = bEvent.message.receiverThread != null ? data.getThread(bEvent.message.receiverThread.id) : null;
		TraceBus bus = data.getBUS(new Long(bEvent.message.bus.id));
		TraceObject fromObject = data.getObject(new Long(bEvent.message.object.id));

		TraceObject toObject = data.getObject(new Long(bEvent.message.callerThread.object.id));
		TraceOperation op = data.getOperation(bEvent.message.operation.classDef.name + bEvent.message.operation.name);
		
		//TODO: MVQ: Review if all objects are required in all draw methods
		switch(bEvent.type)
		{
		case REQUEST: 
			eventViewer.drawMessageRequest(tab, fromCpu, toObject, bus, op);
			break;
		case ACTIVATE:
			eventViewer.drawMessageActivated(tab, fromCpu, fromObject, bus, op);
			break;
		case COMPLETED:
			System.out.println("BusMsg COMPLETED. From Object: "+ fromObject.getId() + " To Object: " + toObject.getId() + " event:" + event.toString());
			eventViewer.drawMessageCompleted(tab, toCpu, callerthread, bus, op, fromObject);
			
	        //If this is a reply to an earlier request then unblock the thread which did the request
	        if(receiverThread != null)
	        {
	        	receiverThread.setStatus(false);
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
