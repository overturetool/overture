/*
 * #%~
 * RT Trace Viewer Plugin
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.rttraceviewer.event;

import org.overture.ide.plugins.rttraceviewer.data.TraceBus;
import org.overture.ide.plugins.rttraceviewer.data.TraceCPU;
import org.overture.ide.plugins.rttraceviewer.data.TraceData;
import org.overture.ide.plugins.rttraceviewer.data.TraceObject;
import org.overture.ide.plugins.rttraceviewer.data.TraceOperation;
import org.overture.ide.plugins.rttraceviewer.data.TraceThread;
import org.overture.ide.plugins.rttraceviewer.data.UnexpectedEventTypeException;
import org.overture.ide.plugins.rttraceviewer.view.GenericTabItem;
import org.overture.interpreter.messages.rtlog.nextgen.INextGenEvent;
import org.overture.interpreter.messages.rtlog.nextgen.NextGenBusMessageEvent;

public class BusMessageEventHandler extends EventHandler {

	public BusMessageEventHandler(TraceData data) {
		super(data);
	}

	@Override
	protected void handle(INextGenEvent event, GenericTabItem tab) {
		
		NextGenBusMessageEvent bEvent = null;
		
		if(event instanceof NextGenBusMessageEvent)
			bEvent = (NextGenBusMessageEvent)event;
		else
			throw new IllegalArgumentException("BusMessageEventHandler expected event of type: " + NextGenBusMessageEvent.class.getName()); 
			
		TraceCPU fromCpu = data.getCPU(new Long(bEvent.message.fromCpu.id));
		TraceCPU toCpu = data.getCPU(new Long(bEvent.message.toCpu.id));
		TraceBus bus = data.getBUS(new Long(bEvent.message.bus.id));
		TraceObject fromObject = data.getObject(new Long(bEvent.message.object.id));
		TraceOperation op = data.getOperation(bEvent.message.operation.classDef.name + bEvent.message.operation.name);
		
		TraceThread currentThread = null;
		Long currentThreadId = toCpu.getCurrentThread();
		if(currentThreadId != null)
		{
			currentThread = data.getThread(currentThreadId);
		}
		
		//TODO: MVQ: Review if all objects are required in all draw methods
		switch(bEvent.type)
		{
		case REQUEST: 
			//Determine from object by thread. Object may be null on some threads (INIT, MAIN etc)
			if(bEvent.message.callerThread.object != null) {
				fromObject = data.getObject(new Long(bEvent.message.callerThread.object.id));
			} else {
				TraceThread fromThread = data.getThread(bEvent.message.callerThread.id);
				fromObject = fromThread.getCurrentObject();				
			}
			eventViewer.drawMessageRequest(tab, fromCpu, fromObject, bus, op);
			break;
		case ACTIVATE:
			eventViewer.drawMessageActivated(tab, fromCpu, fromObject, bus, op);
			break;
		case COMPLETED:	
	        if(bEvent.message.receiverThread != null)
	        {
	        	TraceThread receiverThread = data.getThread(bEvent.message.receiverThread.id);
	        	fromObject = receiverThread.getCurrentObject();
	        	eventViewer.drawMessageCompleted(tab, toCpu, currentThread, bus, op, fromObject);
	        	
	        	//If this is a reply to an earlier request then unblock the thread which did the request
	        	receiverThread.setStatus(false);
	        }
	        else
	        {
	        	eventViewer.drawMessageCompleted(tab, toCpu, currentThread, bus, op, fromObject);
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
