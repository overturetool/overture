package org.overture.ide.plugins.showtraceNextGen.event;

import org.overture.ide.plugins.showtraceNextGen.data.ConjectureData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceBus;
import org.overture.ide.plugins.showtraceNextGen.data.TraceCPU;
import org.overture.ide.plugins.showtraceNextGen.data.TraceData;
import org.overture.ide.plugins.showtraceNextGen.data.TraceObject;
import org.overture.ide.plugins.showtraceNextGen.data.TraceOperation;
import org.overture.ide.plugins.showtraceNextGen.data.TraceThread;
import org.overture.ide.plugins.showtraceNextGen.data.UnexpectedEventTypeException;
import org.overture.ide.plugins.showtraceNextGen.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;

public class BusMessageEventHandler extends EventHandler {

	public BusMessageEventHandler(TraceData data, ConjectureData conjectures) {
		super(data, conjectures);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenBusMessageEvent bEvent = null;
		
		if(event instanceof NextGenBusMessageEvent)
			bEvent = (NextGenBusMessageEvent)event;
		else
			throw new IllegalArgumentException("BusMessageEventHandler expected event of type: " + NextGenBusMessageEvent.class.getName()); 
			
		if(bEvent.message.callerThread.object == null) 
			return; //TODO: MAA: There is no caller thread.object for MAIN and INIT Thread and utils! Causes exception
		
		TraceCPU fromCpu = data.getCPU(new Long(bEvent.message.fromCpu.id));
		TraceCPU toCpu = data.getCPU(new Long(bEvent.message.toCpu.id));
		TraceBus bus = data.getBUS(new Long(bEvent.message.bus.id));
		TraceObject fromObject = data.getObject(new Long(bEvent.message.object.id));
		TraceOperation op = data.getOperation(bEvent.message.operation.classDef.name + bEvent.message.operation.name);
		
		//TODO: MVQ: Review if all objects are required in all draw methods
		switch(bEvent.type)
		{
		case REQUEST: 
			TraceObject toObject = data.getObject(new Long(bEvent.message.callerThread.object.id));
			eventViewer.drawMessageRequest(tab, fromCpu, toObject, bus, op);
			break;
		case ACTIVATE:
			eventViewer.drawMessageActivated(tab, fromCpu, fromObject, bus, op);
			break;
		case COMPLETED:	
			TraceThread callerthread = data.getThread(bEvent.message.callerThread.id);
	        if(bEvent.message.receiverThread != null)
	        {
	        	TraceThread receiverThread = data.getThread(bEvent.message.receiverThread.id);
	        	fromObject = receiverThread.getCurrentObject();
	        	eventViewer.drawMessageCompleted(tab, toCpu, callerthread, bus, op, fromObject);
	        	
	        	//If this is a reply to an earlier request then unblock the thread which did the request
	        	receiverThread.setStatus(false);
	        }
	        else
	        {
	        	eventViewer.drawMessageCompleted(tab, toCpu, callerthread, bus, op, fromObject);
	        }
			break;
		case REPLY_REQUEST:
			throw new UnexpectedEventTypeException("Problem in BusMessageEventHandler. REPLY_REQYEST events should be handled in " + BusMessageReplyEventHandler.class.getName());
		default: 
			throw new UnexpectedEventTypeException("Problem in BusMessageEventHandler. Unexpected bus message event type.");
		}
		
		return;
	}



}
